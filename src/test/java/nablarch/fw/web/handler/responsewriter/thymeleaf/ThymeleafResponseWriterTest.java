package nablarch.fw.web.handler.responsewriter.thymeleaf;

import nablarch.fw.web.servlet.ServletExecutionContext;
import nablarch.test.support.tool.Builder;
import nablarch.test.support.web.servlet.MockServletContext;
import nablarch.test.support.web.servlet.MockServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;

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

    /** 実際に出力を行う{@link TemplateEngine} */
    private final TemplateEngine engine = new TemplateEngine();// リクエストパラメータ

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
        sut.writeResponse("/nablarch/fw/web/handler/responsewriter/thymeleaf/test.html",
                          context);
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
                "</html>")));
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

}