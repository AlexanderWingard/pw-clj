(ns clweb.components.clj-view
  (:require
   #?(:cljs [json-html.core :refer [edn->hiccup]])
   #?(:cljs [reagent.core :refer [create-class]])
   [clweb.util :refer [ws-send]]
   [clojure.walk :refer [prewalk-replace]]))

#?(:clj (def create-class identity))

(defn subscription [state channel]
  (ws-send channel {:server nil})
  (ws-send channel {:server @state}))

(defn render-clojure [data]
  #?(:cljs (edn->hiccup data)))

(defn form [state channel]
  (create-class
   {:component-will-mount
    #(ws-send channel {:action "subscribe" :sub "clj-view"})

    :component-will-unmount
    #(ws-send channel {:action "unsubscribe" :sub "clj-view"})

    :reagent-render
    (fn [state channel]
      [:div.ui.container
       [:h1.ui.header "Debug"]
       (render-clojure @state)])}))
