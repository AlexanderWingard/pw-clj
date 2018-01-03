(ns clweb.core
  (:require
   [clweb.util :as util]
   [cljs.reader :as reader]
   [cljs.test]
   [cljsjs.d3 :as d3]
   [cljsjs.semantic-ui]
   [clojure.string :as str]
   [clojure.reader :refer [register-tag-parser!]]
   [reagent-forms.core :refer [bind-fields]]
   [reagent.core :as reagent :refer [atom]]))
(enable-console-print!)

(register-tag-parser! "object" (fn [arg] (prn-str arg)))

(defn log [arg]
  (js/console.log arg))

(def ws-uri
  (let [location (-> js/window .-location)
        host (-> location .-host)
        protocol (-> location .-protocol (case "http:" "ws:" "https:" "wss:"))]
    (str protocol "//" host "/ws")))
(defonce channel (js/WebSocket. ws-uri))
(defonce state (atom {}))

(defn hashify [m]
  (str "#" (str/join "/" (map #(str/join "/" %) (into [] m)))))

(defn unhashify [s]
  (apply hash-map (filter #(not= "" %) (str/split (subs s 1) #"/"))))

(add-watch state :state-watcher
           (fn [key atom old new]
             (aset js/window "location" "hash" (hashify (:hash new)))))

(defn register []
  (util/ws-send channel (assoc @state :action "register")))

(defn ws-on-message [ws-event]
  (swap! state util/deep-merge (reader/read-string (.-data ws-event))))

(defn ws-open [] ())
(aset channel "onmessage" ws-on-message)
(aset channel "onopen" ws-open)

(defn hash-change []
 (swap! state assoc :hash (unhashify (aget js/window "location" "hash"))))
(aset js/window "onhashchange" hash-change)
(hash-change)

(defn field [type label state path]
  (let [error (get-in @state (conj path :error))]
    [:div.field {:class (when (some? error) "error")}
     [:label label]
     [bind-fields [:input {:field type :id (conj path :value)}] state]
     (when (some? error) [:div.ui.pointing.red.basic.label error])]))

(defn app []
  [:div.ui.container
   (case (get-in @state [:hash "page"])
     "register" ^{:key "register"}
     [:div.ui.segment
      [:div.ui.form
       [field :text "Username" state [:registration-form :username]]
       [field :password "Password" state [:registration-form :password-1]]
       [field :password "Repeat" state [:registration-form :password-2]]
       [:div {:style {:text-align "center"}}
        [:button.ui.button {:on-click register} "Register"]
        [:br]
        [:a {:href "#"} "Back"]]
       ]]
      ^{:key "main"} [:div.ui.segment
      [:div.ui.form
       [field :text "Username" state [:login-form :username]]
       [field :password "Password" state [:login-form :password]]
       [:div {:style {:text-align "center"}}
        [:button.ui.button "Login"]
        [:br]
        [:a {:href "#page/register"} "Register new user"]]
       ]])
   [:div (pr-str @state)]])

(reagent/render [app] (js/document.getElementById "app"))

(defn figwheel-reload [])
