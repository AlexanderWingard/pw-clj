(ns clweb.core
  (:gen-class)
  (:require [clojure.edn :as edn]
            [compojure.core :refer [defroutes GET]]
            [compojure.handler :refer [site]]
            [compojure.route :refer [not-found resources]]
            [org.httpkit.server
             :refer
             [on-close on-receive run-server with-channel]]
            [ring.middleware.cljsjs :refer [wrap-cljsjs]]
            [ring.util.response :refer [resource-response]]))

(defn ws-handler [req]
  (with-channel req channel
    (on-close channel (fn [status] ))
    (on-receive channel (fn [string] ))))

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
