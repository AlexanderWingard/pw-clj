(ns clweb.core
  (:gen-class)
  (:require [clojure.edn :as edn]
            [compojure.core :refer [defroutes GET]]
            [compojure.handler :refer [site]]
            [compojure.route :refer [not-found resources]]
            [clweb.util :refer [ws-send]]
            [clweb.backend-state :as bes]
            [clweb.components.registration :as registration]
            [clweb.components.login :as login]
            [org.httpkit.server :refer [on-close on-receive run-server with-channel]]
            [ring.middleware.cljsjs :refer [wrap-cljsjs]]
            [ring.util.response :refer [resource-response]]
            [clweb.components.user-info :as user-info]))

(defonce state (atom {}))
(add-watch state :state-watcher (fn [_key a old new]
                                  (doseq [[chan subs] (bes/get-subscriptions new)]
                                    (doseq [{kp :keypath
                                             t :target
                                             f :transformer} subs]
                                      (ws-send chan (assoc-in {} t nil))
                                      (ws-send chan (assoc-in {} t (f a)))))))
(swap! state identity)

(defn ws-on-message [channel msg]
  (ws-send
   channel
   (case (:action msg)
     "register" (registration/register-action state channel msg)
     "login" (login/login-action state channel msg)
     "logout" (user-info/logout-action state channel msg)
     {})))

(defn ws-handler [req]
  (with-channel req channel
    (bes/assoc-channel state channel)
    (bes/add-subscription
     state channel
     (bes/subscription [] [:server] deref))
    (on-close channel (fn [status] (bes/dissoc-channel state channel)))
    (on-receive channel (fn [s] (ws-on-message channel (edn/read-string s))))))

(defroutes my-routes
  (GET "/" [] (resource-response "index.html" {:root "public"}))
  (GET "/ws" [] ws-handler)
  (wrap-cljsjs (resources "/"))
  (not-found "Page not found"))

(def ring-handler (site #'my-routes))

(defn -main
  [& args]
  (run-server ring-handler {:port 8080})
  (println "http://localhost:8080"))
