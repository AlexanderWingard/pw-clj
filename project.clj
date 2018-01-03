(defproject clweb "0.1.0-SNAPSHOT"
  :dependencies [
                 [cljsjs/d3 "4.3.0-4"]
                 [cljsjs/semantic-ui "2.2.4-0"]
                 [com.cemerick/piggieback "0.2.1"]
                 [com.rpl/specter "1.0.5"]
                 [compojure "1.5.0"]
                 [figwheel-sidecar "0.5.11"]
                 [binaryage/devtools "0.9.8"]
                 [http-kit "2.1.19"]
                 [json-html "0.4.4"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 [reagent "0.7.0"]
                 [reagent-forms "0.5.29"]
                 [reagent-utils "0.2.1"]
                 [ring-cljsjs "0.1.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 ]
  :plugins [
            [com.jakemccrary/lein-test-refresh "0.20.0"]
            [lein-cljsbuild "1.1.7"]
            [lein-cloverage "1.0.9"]
            [lein-figwheel "0.5.11"]
            [venantius/ultra "0.5.1"]
            ]
  :main ^:skip-aot clweb.core
  :target-path "target/%s"
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :profiles {:uberjar {:aot :all}}
  :figwheel {
             :reload-clj-files true
             :ring-handler clweb.core/ring-handler
             :css-dirs ["resources/public/css"]
             }
  :cljsbuild {
              :builds [{:id "dev"
                        :source-paths ["src/"]
                        :figwheel {
                                   :on-jsload clweb.core/figwheel-reload
                                   }
                        :compiler {:main clweb.core
                                   :asset-path "js/out"
                                   :preloads [devtools.preload]
                                   :output-to "resources/public/js/script.js"
                                   :output-dir "resources/public/js/out"}}
                       {:id "min"
                        :source-paths ["src/"]
                        :compiler {:main clweb.core
                                   :output-to "resources/public/js/script.js"
                                   :output-dir "resources/public/js/out-min"
                                   :source-map-timestamp true
                                   :optimizations :advanced
                                   :pretty-print false}}]})
