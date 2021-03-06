package nablarch.fw.web.handler.responsewriter.thymeleaf;

import nablarch.core.ThreadContext;
import nablarch.fw.web.handler.responsewriter.CustomResponseWriter;
import nablarch.fw.web.servlet.ServletExecutionContext;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

/**
 * Thymeleafを使用する{@link CustomResponseWriter}実装クラス。
 * <p>
 * 本実装では、引数で与えられたパスが、処理対象パス判定用の正規表現にマッチした場合、
 * 処理対象と判定する。
 * 例えば、{@link #setPathPattern(String)}に"{@literal /template/.*\.html}"を設定した場合、
 * パスが"/template/foo/bar.html"の時、処理対象と判定される。
 * pathPatternプロパティにはデフォルト値として"{@literal .*\.html}"が設定されている。
 * </p>
 * <p>
 * Thymeleafでは、テンプレートのパスを解決する際、サフィックスを省略できるが、
 * 本クラスを使用する場合はサフィックスの省略は行わないこと。
 * <ul>
 *     <li>OK: {@code return new HttpResponse("/path/to/template.html");}</li>
 *     <li>NG: {@code return new HttpResponse("/path/to/template");}</li>
 * </ul>
 * サフィックスを省略した場合、セッションストアからリクエストスコープへの移送が行われなくなる。
 * </p>
 *
 * @author Tsuyoshi Kawasaki
 * @see org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver#setSuffix(java.lang.String)
 */
public class ThymeleafResponseWriter implements CustomResponseWriter {

    /** テンプレートエンジン */
    private TemplateEngine templateEngine;

    /** 処理対象パス判定に使用する正規表現 */
    private Pattern pathPattern = Pattern.compile(".*\\.html");

    @Override
    public boolean isResponsibleTo(String pathToTemplate, ServletExecutionContext context) {
        return pathPattern.matcher(pathToTemplate).matches();
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

    /**
     * 処理対象パス判定に使用する正規表現を設定する。
     *
     * @param pathPattern 処理対象パス判定用の正規表現
     */
    public void setPathPattern(String pathPattern) {
        this.pathPattern = Pattern.compile(pathPattern);
    }
}
