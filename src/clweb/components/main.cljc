(ns clweb.components.main
  (:require
   [clweb.components.registration :as registration]
   [clweb.components.login :as login]))

(defn render [state channel]
  [:div.ui.container
   (case (get-in @state [:hash "page"])
     "register" (registration/form state channel)
                (login/form state channel))
   [:div (pr-str @state)]])
