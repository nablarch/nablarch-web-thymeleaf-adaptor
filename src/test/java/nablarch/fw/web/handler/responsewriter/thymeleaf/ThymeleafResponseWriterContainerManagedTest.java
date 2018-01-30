package nablarch.fw.web.handler.responsewriter.thymeleaf;

import nablarch.core.ThreadContext;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.Builder;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpRequestHandler;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.MockHttpRequest;
import nablarch.fw.web.handler.HttpResponseHandler;
import nablarch.fw.web.servlet.ServletExecutionContext;
import nablarch.test.support.SystemRepositoryResource;
import nablarch.test.support.web.servlet.MockServletContext;
import nablarch.test.support.web.servlet.MockServletRequest;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ThymeleafResponseWriterContainerManagedTest {

    /** モックの{@link javax.servlet.ServletRequest} */
    private final MockServletRequest mockReq = new MockServletRequest();

    /** モックの{@link javax.servlet.ServletResponse} */
    private final WritableMockResponse mockRes = new WritableMockResponse();

    /** 実行コンテキスト */
    private final ServletExecutionContext context = new ServletExecutionContext(mockReq, mockRes, new MockServletContext());

    @Rule
    public SystemRepositoryResource repo = new SystemRepositoryResource(
            "nablarch/fw/web/handler/responsewriter/thymeleaf/ThymeleafResponseWriterContainerManagedTest.xml");

    /**
     * {@link SystemRepository}に登録したコンポーネントとして実行できること。
     *
     * {@link HttpResponseHandler}に設定した状態で、
     * {@link ThymeleafResponseWriter}によるレスポンス出力ができること。
     */
    @Test
    public void testInRepository() {
        HttpResponseHandler httpResponseHandler = SystemRepository.get("httpResponseHandler");

        // パラメータ
        mockReq.getParameterMap().put("msgInParam", new String[] {"I am parameter."} );

        context.addHandler(httpResponseHandler);
        context.addHandler(new HttpRequestHandler() {
            @Override
            public HttpResponse handle(HttpRequest request, ExecutionContext ctx) {
                ThreadContext.setLanguage(null); // defaultのLocaleを使用する
                ctx.setRequestScopedVar("sayHelloTo", "Nabchan");
                return new HttpResponse(200,
                                        "/thymeleaf/test.html");  // prefixを使用
            }
        });
        // 実行
        context.handleNext(new MockHttpRequest("GET / HTTP/1.1"));

        // 検証
        String bodyString = mockRes.getBodyString();
        assertThat(bodyString, is(Builder.lines(
                "<!DOCTYPE html>",
                "<html>",
                "<body>",
                "<p>I am parameter.</p>",    // リクエストパラメータ
                "<p>Nabchan</p>",             // リクエストスコープ
                "<p>Hello there!</p>",       // メッセージ
                "</body>",
                "</html>"
        )));

    }

}
