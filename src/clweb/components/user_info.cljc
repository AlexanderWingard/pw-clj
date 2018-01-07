(ns clweb.components.user-info
  (:require [clweb.util :refer [action]]
            [clweb.backend-state :as bes]))

(defn logout-action [state channel msg]
  (bes/logout state channel)
  {:logged-in nil :login-form nil})

(defn form [state channel]
  [:div.ui.segment
   [:div (str "Logged in as " (:logged-in @state))]
   [:button.ui.button {:on-click #(action state channel "logout")} "Logout"]])
