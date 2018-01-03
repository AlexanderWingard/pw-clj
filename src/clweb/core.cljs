(ns clweb.core
  (:require
   [clweb.util :as util]
   [clweb.location-hash :refer [hashify unhashify]]
   [clweb.components.main :as main]
   [cljs.reader :as reader]
   [cljs.test]
   [cljsjs.d3 :as d3]
   [cljsjs.semantic-ui]
   [clojure.string :as str]
   [clojure.reader :refer [register-tag-parser!]]
   [reagent.core :as reagent :refer [atom]]))
(enable-console-print!)
(defn log [arg]
  (js/console.log arg))

(register-tag-parser! "object" (fn [arg] (prn-str arg)))

(def ws-uri
  (let [location (-> js/window .-location)
        host (-> location .-host)
        protocol (-> location .-protocol (case "http:" "ws:" "https:" "wss:"))]
    (str protocol "//" host "/ws")))
(defonce channel (js/WebSocket. ws-uri))
(defonce state (atom {}))

(add-watch state :state-watcher
           (fn [key atom old new]
             (aset js/window "location" "hash" (hashify (:hash new)))))

(defn ws-on-message [ws-event]
  (swap! state util/deep-merge (reader/read-string (.-data ws-event))))
(aset channel "onmessage" ws-on-message)
(defn ws-open [] ())
(aset channel "onopen" ws-open)

(defn hash-change []
 (swap! state assoc :hash (unhashify (aget js/window "location" "hash"))))
(aset js/window "onhashchange" hash-change)
(hash-change)

(reagent/render [main/render state channel]
                (js/document.getElementById "app"))

(defn figwheel-reload [])
