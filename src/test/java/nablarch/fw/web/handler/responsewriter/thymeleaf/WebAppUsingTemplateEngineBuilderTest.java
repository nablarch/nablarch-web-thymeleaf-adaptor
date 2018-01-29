package nablarch.fw.web.handler.responsewriter.thymeleaf;

import nablarch.core.repository.SystemRepository;
import nablarch.fw.web.servlet.WebFrontController;
import nablarch.test.support.SystemRepositoryResource;
import nablarch.test.support.web.servlet.MockServletContext;
import nablarch.test.support.web.servlet.MockServletFilterConfig;
import org.junit.Rule;
import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.FilterConfig;
import java.util.Set;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

/**
 * {@link WebAppUsingTemplateEngineBuilder}のテストクラス。
 *
 * @author Tsuyoshi Kawasaki
 */
public class WebAppUsingTemplateEngineBuilderTest {

    /** テスト対象 */
    private final WebAppUsingTemplateEngineBuilder sut = new WebAppUsingTemplateEngineBuilder();

    @Rule
    public SystemRepositoryResource repo = new SystemRepositoryResource(
            "nablarch/fw/web/handler/responsewriter/thymeleaf/WebAppUsingTemplateEngineBuilderTest.xml");

    @Test
    public void テンプレートエンジンの組み立てができること() {
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(new FileTemplateResolver());  // 任意のリゾルバを設定しておく
        sut.setTemplateEngine(engine);
        TemplateEngine templateEngine = sut.build();
        Set<ITemplateResolver> resolvers = templateEngine.getTemplateResolvers();
        assertThat("元々TemplateEngineに設定してあるリゾルバが保持されていること",
                   resolvers, hasItem(isA(FileTemplateResolver.class)));
        assertThat("ServletContextTemplateResolverが追加されていること",
                   resolvers, hasItem(isA(ServletContextTemplateResolver.class)));

    }

    @Test(expected = IllegalStateException.class)
    public void webFrontControllerが見つからないとき例外が発生すること() {
        SystemRepository.clear();  // WebFrontControllerが登録されていない状態を作る
        sut.build();
    }


    public static class MockWebFrontController extends WebFrontController {
        @Override
        public FilterConfig getServletFilterConfig() {
            MockServletFilterConfig mockConfig = new MockServletFilterConfig();
            mockConfig.setServletContext(new MockServletContext());
            return mockConfig;
        }
    }

}