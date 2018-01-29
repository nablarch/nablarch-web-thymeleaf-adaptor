package nablarch.fw.web.handler.responsewriter.thymeleaf;

import nablarch.core.repository.SystemRepository;
import nablarch.fw.web.handler.responsewriter.thymeleaf.TemplateEngineBuilder.AbstractTemplateEngineBuilder;
import nablarch.fw.web.servlet.WebFrontController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * {@link ServletContextTemplateResolver}を追加する{@link TemplateEngineBuilder}実装クラス。
 *
 * webapp配下にテンプレートを配置したい場合は本クラスを使用する。
 *
 * {@link ServletContextTemplateResolver}はコンストラクタに{@link ServletContext}を要求するため、
 * DIコンテナに登録されるコンポーネントが{@link ServletContext}を取得することができない。
 *
 * 処理順序としては以下のようになる。
 * <ol>
 * <li>{@link nablarch.fw.web.servlet.NablarchServletContextListener#contextInitialized(ServletContextEvent)}</li>
 * <li>{@link nablarch.core.repository.di.DiContainer#reload()}</li>
 * <li>{@link nablarch.core.repository.initialization.Initializable#initialize()} ()}</li>
 * <li>{@link SystemRepository#load(nablarch.core.repository.ObjectLoader)}</li>
 * </ol>
 *
 * 上記の通り、{@link WebFrontController}を取得するには{@link SystemRepository}構築後に行う必要がある。
 * このため、本クラスの{@link #build()}呼び出しは、{@link SystemRepository}構築後でなければならない。
 *
 * @see ServletContextTemplateResolver#ServletContextTemplateResolver(ServletContext)
 */
public class WebAppUsingTemplateEngineBuilder extends AbstractTemplateEngineBuilder {

    @Override
    void process(TemplateEngine templateEngine) {
        ServletContextTemplateResolver additionalResolver = createServletContextTemplateResolver();
        templateEngine.addTemplateResolver(additionalResolver);
    }

    /**
     * {@link ServletContextTemplateResolver}インスタンスを生成する。
     *
     * @return {@link ServletContextTemplateResolver}インスタンス
     */
    private ServletContextTemplateResolver createServletContextTemplateResolver() {
        WebFrontController webFrontController = SystemRepository.get("webFrontController");
        if (webFrontController == null) {
            throw new IllegalStateException("could not find component 'webFrontController'.");
        }
        ServletContext servletContext = webFrontController.getServletFilterConfig().getServletContext();
        return new ServletContextTemplateResolver(servletContext);
    }
}
