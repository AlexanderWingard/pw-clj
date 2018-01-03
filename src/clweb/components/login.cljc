(ns clweb.components.login
  (:require
   [clweb.form-util :refer [field]]
   [clweb.util :refer [action]]))

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
