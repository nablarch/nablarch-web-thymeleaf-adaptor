package nablarch.fw.web.handler.responsewriter.thymeleaf;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

/**
 * {@link TemplateEngine}の組み立てを行うインタフェース。
 *
 * {@link ServletContextTemplateResolver}のような、DIコンテナでは扱えないクラスを使って
 * {@link TemplateEngine}を構築したい場合は、本インタフェースを利用してインスタンスを生成する。
 *
 * @author Tsuyoshi Kawasaki
 */
public interface TemplateEngineBuilder {

    /**
     * {@link TemplateEngine}を組み立てる。
     *
     * インスタンス生成とオブジェクトグラフの構築を行う。
     * 本メソッドが返却するインスタンスは、即座に使用可能な状態な状態となっている。
     *
     * @return {@link TemplateEngine}
     */
    TemplateEngine build();

    /**
     * {@link TemplateEngineBuilder}の抽象実装クラス。
     *
     * 特に何もせずに設定された{@link TemplateEngine}を返却する。
     * {@link TemplateEngine}が設定されていない場合、新しいインスタンスを返却する。
     *
     * 本クラスはスレッドセーフではない。呼び出し元で同期化を行うこと。
     */
    abstract class AbstractTemplateEngineBuilder implements TemplateEngineBuilder {

        /** テンプレートエンジン */
        private TemplateEngine templateEngine;

        @Override
        public TemplateEngine build() {
            if (templateEngine == null) {
                templateEngine = new TemplateEngine();
            }
            process(templateEngine);
            return templateEngine;
        }

        /**
         * {@link TemplateEngine}インスタンスの加工を行う。
         *
         * 本メソッドでインスタンスに対して任意の設定を行う。
         * 本メソッドで処理されたインスタンスが、最終的に{@link #build()}で返却される。
         *
         * @param templateEngine {@link TemplateEngine}インスタンス
         */
        abstract void process(TemplateEngine templateEngine);

        /**
         * {@link TemplateEngine}を設定する。
         *
         * @param templateEngine {@link TemplateEngine}
         */
        public void setTemplateEngine(TemplateEngine templateEngine) {
            this.templateEngine = templateEngine;
        }
    }
}
