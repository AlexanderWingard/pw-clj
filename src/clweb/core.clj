(ns clweb.core
  (:gen-class)
  (:require [clojure.edn :as edn]
            [compojure.core :refer [defroutes GET]]
            [compojure.handler :refer [site]]
            [compojure.route :refer [not-found resources]]
            [clweb.util :refer [ws-send]]
            [clweb.backend-state :as bes]
            [clweb.components.registration :as registration]
            [clweb.components.clj-view :as clj-view]
            [clweb.components.login :as login]
            [org.httpkit.server :refer [on-close on-receive run-server with-channel]]
            [ring.middleware.cljsjs :refer [wrap-cljsjs]]
            [ring.util.response :refer [resource-response]]
            [clweb.components.user-info :as user-info]))

(defonce state (atom {}))
(add-watch state :state-watcher (fn [_key a old new]
                                  (doseq [[channel subs] (bes/get-subscriptions new)]
                                    (doseq [s subs]
                                      (s a channel)))))
(swap! state identity)

(defn subscribe [m state channel msg]
  (bes/add-subscription state channel (get m (:sub msg)))
  {})

(defn unsubscribe [m state channel msg]
  (bes/delete-subscription state channel (get m(:sub msg)))
  {})

(def sub-map {"clj-view" clj-view/subscription
              "user-info" user-info/subscription})

(def action-map {"register" registration/register-action
                 "login" login/login-action
                 "logout" user-info/logout-action
                 "unsubscribe" (partial unsubscribe sub-map)
                 "subscribe" (partial subscribe sub-map)})

(defn ws-on-message [channel msg]
  (when-let [action (get action-map (:action msg))]
    (ws-send channel (action state channel msg))))

(defn ws-handler [req]
  (with-channel req channel
    (bes/assoc-channel state channel)
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
