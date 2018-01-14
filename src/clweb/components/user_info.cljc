(ns clweb.components.user-info
  (:require [clweb.util :refer [action ws-send]]
            [clweb.backend-state :as bes]
            #?(:cljs [reagent.core :refer [create-class]])))

#?(:clj (def create-class identity))

(defn logout-action [state channel msg]
  (bes/logout state channel)
  {:logged-in nil :login-form nil})

(defn subscription [state channel]
  (ws-send channel {:user-info {:sessions (count (bes/get-user-sessions state (bes/get-channel-user state channel)))}}))

(defn form [state channel]
  (create-class
   {:component-will-mount
    #(ws-send channel {:action "subscribe" :sub "user-info"})

    :component-will-unmount
    #(ws-send channel {:action "unsubscribe" :sub "user-info"})

    :reagent-render
    (fn [state channel]
      [:div.ui.segment
       [:div (str "Logged in as " (:logged-in @state))]
       [:div (str "You have " (get-in @state [:user-info :sessions]) " sessions")]
       [:button.ui.button {:on-click #(action state channel "logout")} "Logout"]])}))
