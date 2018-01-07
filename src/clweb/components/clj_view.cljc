(ns clweb.components.clj-view
  (:require
   #?(:cljs [json-html.core :refer [edn->hiccup]])
   [clojure.walk :refer [prewalk-replace]]))

(defn render-clojure [data]
  (prewalk-replace
   {:table.jh-type-object :table.jh-type-object.ui.celled.table}
   #?(:cljs (edn->hiccup data))))

(defn form [data]
  [:div.ui.container
   [:h1.ui.header "Debug"]
   (render-clojure data)])
