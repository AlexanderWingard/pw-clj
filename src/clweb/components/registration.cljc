(ns clweb.components.registration
  (:require
   [clweb.form-util :as fu :refer [field]]
   [clweb.util :refer [ws-send]]
   [clweb.util :refer [action]]
   [clweb.backend-state :as bes]))

(defn validate [state]
  (-> {}
      (assoc-in [:registration-form :username :error]
                (when (< (count (get-in state [:registration-form :username :value])) 3)
                  "Username too short"))
      (assoc-in [:registration-form :password-1 :error]
                (when (< (count (get-in state [:registration-form :password-1 :value])) 4)
                  "Password too short"))
      (assoc-in [:registration-form :password-2 :error]
                (when (not= (get-in state [:registration-form :password-1 :value])
                            (get-in state [:registration-form :password-2 :value]))
                  "Passwords don't match"))))

(defn register-action [channel msg]
  (let [errors (validate msg)]
    (if (= 0 (count (fu/errors errors)))
      (do
        (let [uid (bes/register-user
                   channel
                   (get-in msg [:registration-form :username :value])
                   (get-in msg [:registration-form :password-1 :value]))]
          (ws-send channel {:hash nil :registration-form nil :logged-in uid})))
      (ws-send channel errors))))

(defn form [state channel]
  ^{:key "register"}
  [:div.ui.segment
   [:div.ui.form
    [field :text "Username" state [:registration-form :username]]
    [field :password "Password" state [:registration-form :password-1]]
    [field :password "Repeat" state [:registration-form :password-2]]
    [:div {:style {:text-align "center"}}
     [:button.ui.button {:on-click #(action state channel "register")} "Register"]
     [:br]
     [:a {:href "#"} "Back"]]]])
