(ns clweb.components.main
  (:require
   [clweb.components.registration :as registration]
   [clweb.components.clj-view :as clj-view]
   [clweb.components.user-info :as user-info]
   [clweb.components.login :as login]))

(defn render [state channel]
  [:div.ui.container
   (case (get-in @state [:hash "page"])
     "register" [registration/form state channel]
     (if (nil? (:logged-in @state))
       [login/form state channel]
       [user-info/form state channel]))
   [clj-view/form @state]])
