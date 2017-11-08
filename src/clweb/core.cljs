(ns clweb.core
  (:require
   [cljs.reader :as reader]
   [cljs.test]
   [cljsjs.d3 :as d3]
   [cljsjs.semantic-ui]
   [clojure.string :as str]
   [clojure.reader :refer [register-tag-parser!]]
   [reagent.core :as reagent :refer [atom]]))
(enable-console-print!)


(register-tag-parser! "object" (fn [arg] (prn-str arg)))

(def ws-uri
  (let [location (-> js/window .-location)
        host (-> location .-host)
        protocol (-> location .-protocol (case "http:" "ws:" "https:" "wss:"))]
    (str protocol "//" host "/ws")))
(defonce channel (js/WebSocket. ws-uri))
(defonce state (atom {:server-state nil}))


(defn ws-on-message [ws-event]
  (swap! state assoc-in [:server-state] (reader/read-string (.-data  ws-event))))

(defn ws-open [] ())
(aset channel "onmessage" ws-on-message)
(aset channel "onopen" ws-open)

(defn hash-change [])
(aset js/window "onhashchange" hash-change)
(hash-change)

(defn app []
  [:div (pr-str @state)])

(reagent/render [app] (js/document.getElementById "app"))

(defn figwheel-reload [])
