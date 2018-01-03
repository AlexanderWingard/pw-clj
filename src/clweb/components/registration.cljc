(ns clweb.components.registration
  (:require
   [clweb.form-util :refer [field]]
   [clweb.util :refer [action]]))

(defn validate [state]
  (-> {}
      (assoc-in [:registration-form :username :error]
                (when (> 3 (count (get-in state [:registration-form :username :value])))
                  "Username too short"))
      (assoc-in [:registration-form :password-2 :error]
                (when (not= (get-in state [:registration-form :password-1 :value])
                            (get-in state [:registration-form :password-2 :value]))
                  "Passwords don't match"))
      (assoc-in [:login-form :username :value] (get-in state [:registration-form :username :value]))))

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
