(ns clweb.components.login
  (:require
   [clweb.form-util :as fu :refer [field]]
   [clweb.backend-state :as bes]
   [clweb.util :refer [action]]))

(defn validate [msg user]
  (-> {}
      (assoc-in [:login-form :username :error]
                (when (nil? user) "User not found"))
      (assoc-in [:login-form :password :error]
                (when (and (some? user)
                           (not= (get-in msg [:login-form :password :value])
                                 (:password user)))
                  "Wrong password"))))

(defn login-action [state channel msg]
  (let [[uid user] (bes/get-user-by-name state (get-in msg [:login-form :username :value]))
        errors (validate msg user)]
    (if (= 0 (count (fu/errors errors)))
      {:logged-in uid :login-form nil}
      errors)))

(defn form [state channel]
  ^{:key "main"}
  [:div.ui.segment
   [:div.ui.form
    [field :text "Username" state [:login-form :username]]
    [field :password "Password" state [:login-form :password]]
    [:div {:style {:text-align "center"}}
     [:button.ui.button {:on-click #(action state channel "login")} "Login"]
     [:br]
     [:a {:href "#page/register"} "Register new user"]]]])
