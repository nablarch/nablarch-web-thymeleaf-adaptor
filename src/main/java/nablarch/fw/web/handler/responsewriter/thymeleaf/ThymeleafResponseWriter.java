package nablarch.fw.web.handler.responsewriter.thymeleaf;

import nablarch.core.ThreadContext;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.handler.responsewriter.CustomResponseWriter;
import nablarch.fw.web.servlet.ServletExecutionContext;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

/**
 * Thymeleafを使用する{@link CustomResponseWriter}実装クラス。
 *
 * @author Tsuyoshi Kawasaki
 */
public class ThymeleafResponseWriter implements CustomResponseWriter {

    /** テンプレートエンジン */
    private TemplateEngine templateEngine;

    /**
     * 処理対象パス判定に使用する正規表現
     *
     * @see #isResponsibleTo(String, ServletExecutionContext)
     */
    private Pattern pattern = Pattern.compile(".*\\.html");

    /**
     * {@inheritDoc}
     *
     * 本実装では、レスポンスのコンテンツパスが、
     * 設定した正規表現に合致した場合に、処理対象と判定する。
     *
     * 例えば、pathPatternに"{@literal /template/.*\.html}を設定した場合、
     * パスが"/template/foo/bar.html"の時、処理対象と判定される。
     */
    @Override
    public boolean isResponsibleTo(String pathToTemplate, ServletExecutionContext context) {
        return pattern.matcher(pathToTemplate).matches();
    }

    @Override
    public void writeResponse(String pathToTemplate, ServletExecutionContext context) throws IOException {
        WebContext webContext = new WebContext(context.getServletRequest(),
                                               context.getServletResponse(),
                                               context.getServletContext(),
                                               ThreadContext.getLanguage());   // Locale
        Writer writer = context.getServletResponse().getWriter();

        templateEngine.process(pathToTemplate, webContext, writer);
    }

    /**
     * {@link TemplateEngine}を設定する。
     *
     * @param templateEngine {@link TemplateEngine}
     */
    public void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }
}
