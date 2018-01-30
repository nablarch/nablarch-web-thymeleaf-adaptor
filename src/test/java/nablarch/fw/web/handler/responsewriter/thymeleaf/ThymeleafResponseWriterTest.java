package nablarch.fw.web.handler.responsewriter.thymeleaf;

import nablarch.core.ThreadContext;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpRequestHandler;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.MockHttpRequest;
import nablarch.fw.web.handler.HttpResponseHandler;
import nablarch.fw.web.servlet.ServletExecutionContext;
import nablarch.test.support.tool.Builder;
import nablarch.test.support.web.servlet.MockServletContext;
import nablarch.test.support.web.servlet.MockServletRequest;
import nablarch.test.support.web.servlet.MockServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * {@link ThymeleafResponseWriter}のテストクラス。
 *
 * @author Tsuyoshi Kawasaki
 */
public class ThymeleafResponseWriterTest {

    /** テスト対象 */
    private final ThymeleafResponseWriter sut = new ThymeleafResponseWriter();

    /** モックの{@link javax.servlet.ServletRequest} */
    private final MockServletRequest mockReq = new MockServletRequest();

    /** モックの{@link javax.servlet.ServletResponse} */
    private final WritableMockResponse mockRes = new WritableMockResponse();

    /** 実行コンテキスト */
    private final ServletExecutionContext context = new ServletExecutionContext(mockReq, mockRes, new MockServletContext());

    /** HTTPレスポンス */
    private final HttpResponse response = new HttpResponse(200,
                                                           "/nablarch/fw/web/handler/responsewriter/thymeleaf/test.html");

    /** 実際に出力を行う{@link TemplateEngine} */
    private final TemplateEngine engine = new TemplateEngine();// リクエストパラメータ

    /** テンプレートエンジン出力の期待値 */
    private static final String EXPECTED_BODY_STRING = Builder.lines(
            "<!DOCTYPE html>",
            "<html>",
            "<body>",
            "<p>I am parameter.</p>",    // リクエストパラメータ
            "<p>Nabchan</p>",             // リクエストスコープ
            "<p>Hello there!</p>",       // メッセージ
            "</body>",
            "</html>");

    /** {@link org.thymeleaf.templateresolver.ITemplateResolver}の設定をする */
    @Before
    public void setUp() {
        engine.setTemplateResolver(new ClassLoaderTemplateResolver());
        sut.setTemplateEngine(engine);
    }

    /** {@link ThymeleafResponseWriter}単体で動作すること. */
    @Test
    @SuppressWarnings("unchecked")
    public void testThymeleafResponseWriter() throws IOException {
        // パラメータ
        mockReq.getParameterMap().put("msgInParam", new String[] {"I am parameter."} );
        // リクエストスコープ
        context.setRequestScopedVar("sayHelloTo", "Nabchan");
        sut.writeResponse(response.getContentPath().getPath(),
                          context);
        // 検証
        String bodyString = mockRes.writer.toString();
        assertThat(bodyString, is(EXPECTED_BODY_STRING));
    }


    /**
     *  {@link HttpResponseHandler}に設定した状態で、
     * {@link ThymeleafResponseWriter}によるレスポンス出力ができること。
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testWithinHttpResponseHandler() {

        // パラメータ
        mockReq.getParameterMap().put("msgInParam", new String[] {"I am parameter."} );

        // ハンドラ
        HttpResponseHandler httpResponseHandler = new HttpResponseHandler();
        httpResponseHandler.setCustomResponseWriter(sut);
        context.addHandler(httpResponseHandler);
        context.addHandler(new HttpRequestHandler() {
            @Override
            public HttpResponse handle(HttpRequest request, ExecutionContext ctx) {
                ThreadContext.setLanguage(null); // defaultのLocaleを使用する
                ctx.setRequestScopedVar("sayHelloTo", "Nabchan");
                return response;
            }
        });
        // 実行
        context.handleNext(new MockHttpRequest("GET / HTTP/1.1"));

        // 検証
        String bodyString = mockRes.writer.toString();
        assertThat(bodyString, is(EXPECTED_BODY_STRING));
    }

    /** {@link ThymeleafResponseWriter#setPathPattern(String)}で設定した正規表現を用いて、処理対象かどうかの判定ができること。 */
    @Test
    public void testIsResponsibleTo() {
        sut.setPathPattern("/template/.*\\.html");

        assertThat(sut.isResponsibleTo("/template/foo/bar/buz.html", context),
                   is(true));

        assertThat(sut.isResponsibleTo("/index.html", context),
                   is(false));
    }

    private static class WritableMockResponse extends MockServletResponse {
        private final StringWriter writer = new StringWriter();
        @Override
        public PrintWriter getWriter() {
            return new PrintWriter(writer);
        }
    }
}