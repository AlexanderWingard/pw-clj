(ns clweb.backend-state
  (:require [clweb.util :refer [ws-send]]
            [clojure.data :refer [diff]]
            [com.rpl.specter :as s]))

(defn setup-watcher [state]
  (add-watch
   state :state-watcher
   (fn [_key _atom old new]
     (doseq [[chan _]
             (s/select [:sessions
                        s/ALL
                        (s/collect-one s/FIRST)
                        s/LAST
                        :subscriptions
                        (s/pred= true)]
                       new)]
       (ws-send chan {:state nil})
       (ws-send chan {:state new})))))

(defn register-user [state channel username password]
  (let [uid username]
    (swap! state update-in [:users]
           assoc uid {:username username :password password})
    (swap! state
           assoc-in [:sessions channel :user] uid)
    uid))

(defn logout [state channel]
  (swap! state update-in [:sessions channel] dissoc :user))

(defn get-user-by-name [state username]
  (s/select-one [:users
                 s/ALL
                 (s/collect-one s/FIRST)
                 s/LAST
                 #(= username (:username %))]
                @state))

(defn add-subscription [state channel]
  (swap! state assoc-in [:sessions channel :subscriptions] true))

(defn get-subscriptions [state]
  (s/select [:sessions
             s/ALL
             (s/collect-one s/FIRST)
             s/LAST
             :subscriptions
             (s/pred= true)]
            state))

(defn assoc-channel [state channel]
  (swap! state assoc-in [:sessions channel] {}))

(defn dissoc-channel [state channel]
  (swap! state update-in [:sessions] dissoc channel))
