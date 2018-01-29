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
 * 実際の出力に使用する{@link TemplateEngine}インスタンスは、{@link #setTemplateEngine(TemplateEngine)}で設定する。
 * このプロパティが設定されていないは場合、{@link #setTemplateEngineBuilder(TemplateEngineBuilder)}
 * で設定されたビルダーを使用してインスタンス生成を行う。
 * このため、この2つのプロパティはいずれかが設定されていなければならない。
 * （両方が設定されている場合は、{@link #setTemplateEngine(TemplateEngine)}の設定が使用される）
 *
 * @author Tsuyoshi Kawasaki
 */
public class ThymeleafResponseWriter implements CustomResponseWriter {

    /** テンプレートエンジン */
    private TemplateEngine templateEngine;

    /** テンプレートエンジン構築クラス */
    private TemplateEngineBuilder templateEngineBuilder;

    /**
     * 処理対象パス判定に使用する正規表現
     *
     * @see #isResponsibleTo(HttpResponse, ServletExecutionContext)
     */
    private Pattern pattern = Pattern.compile(".*\\.html");

    /**
     * {@inheritDoc}
     *
     * 本実装では、レスポンスのコンテンツパスが、
     * 設定した正規表現に合致した場合に、処理対象と判定する。
     *
     * 例えば、pathPatternに"{@literal /template/.*\.html}を設定した場合、
     * コンテンツパスが"/template/foo/bar.html"の時、処理対象と判定される。
     *
     * @see HttpResponse#getContentPath()
     */
    @Override
    public boolean isResponsibleTo(HttpResponse response, ServletExecutionContext context) {
        String path = response.getContentPath().getPath();
        return pattern.matcher(path).matches();
    }

    @Override
    public void writeResponse(HttpResponse response, ServletExecutionContext context) throws IOException {

        String pathToTemplate = response.getContentPath().getPath();
        WebContext webContext = new WebContext(context.getServletRequest(),
                                               context.getServletResponse(),
                                               context.getServletContext(),
                                               ThreadContext.getLanguage());   // Locale
        Writer writer = context.getServletResponse().getWriter();

        TemplateEngine engine = getTemplateEngine();
        engine.process(pathToTemplate, webContext, writer);
    }

    /**
     * {@link TemplateEngine}を取得する。
     *
     * TemplateEngineのインスタンスが設定されている場合は、それをそのまま返却する。
     * 設定されていない場合、ファクトリでインスタンス生成し返却する。
     *
     * フィールドのnull判定と代入が必要なため同期化をしている。
     *
     * @return テンプレートエンジン
     */
    private synchronized TemplateEngine getTemplateEngine() {
        if (templateEngine != null) {
            return templateEngine;
        }
        if (templateEngineBuilder == null) {
            throw new IllegalStateException("either templateEngine or templateEngineBuilder must be set.");
        }
        templateEngine = templateEngineBuilder.build();
        return templateEngine;
    }

    /**
     * {@link TemplateEngineBuilder}を設定する。
     *
     * {@link #setTemplateEngine(TemplateEngine)}で設定できないような
     * 複雑なオブジェクト生成ロジックが必要な場合は、本プロパティにビルダークラスを設定する。
     *
     * @param templateEngineBuilder テンプレートエンジンファクトリ
     */
    public void setTemplateEngineBuilder(TemplateEngineBuilder templateEngineBuilder) {
        this.templateEngineBuilder = templateEngineBuilder;
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
