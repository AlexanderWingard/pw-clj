(ns clweb.backend-state
  (:require [clweb.util :refer [ws-send]]
            [clojure.data :refer [diff]]
            [com.rpl.specter :as s]))

(defonce sessions (atom {}))
(defonce state (atom {}))

(add-watch sessions :sessions-watcher
           (fn [_key _atom old new]
             (doseq [[chan _]
                     (s/select [s/ALL
                                (s/collect-one s/FIRST)
                                s/LAST
                                :subscriptions
                                (s/pred= true)]
                               @sessions)]
               (ws-send chan {:sessions nil})
               (ws-send chan {:sessions new}))))

(add-watch state :state-watcher
           (fn [_key _atom old new]
             (doseq [[chan _]
                     (s/select [s/ALL
                                (s/collect-one s/FIRST)
                                s/LAST
                                :subscriptions
                                (s/pred= true)]
                               @sessions)]
               (ws-send chan {:state nil})
               (ws-send chan {:state new}))))

(defn register-user [channel username password]
  (let [uid username]
    (swap! state update-in [:users]
           assoc uid {:username username :password password})
    (swap! sessions
           assoc-in [channel :user] uid)
    uid))

(defn get-user-by-name [username]
  (s/select-one [:users
                 s/ALL
                 (s/collect-one s/FIRST)
                 s/LAST
                 #(= username (:username %))]
                @state))

(defn add-subscription [channel]
  (swap! sessions assoc-in [channel :subscriptions] true))

(defn assoc-channel [channel]
  (swap! sessions assoc channel {}))

(defn dissoc-channel [channel]
  (swap! sessions dissoc channel))
