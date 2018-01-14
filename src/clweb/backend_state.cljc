(ns clweb.backend-state
  (:require [clweb.util :refer [ws-send]]
            [clojure.data :refer [diff]]
            [com.rpl.specter :as s]))

(defn login [state channel uid]
  (swap! state
         assoc-in [:sessions channel :user] uid))

(defn logout [state channel]
  (swap! state update-in [:sessions channel] dissoc :user))

(defn register-user [state channel username password]
  (let [uid username]
    (swap! state update-in [:users]
           assoc uid {:username username :password password})
    (login state channel uid)
    uid))

(defn get-user-by-name [state username]
  (s/select-one [:users
                 s/ALL
                 (s/collect-one s/FIRST)
                 s/LAST
                 #(= username (:username %))]
                @state))

(defn get-channel-user [state channel]
  (get-in @state [:sessions channel :user]))

(defn add-subscription [state channel sub]
  (swap! state (fn [s] (s/setval [:sessions
                                  s/ALL
                                  (s/selected? s/FIRST (s/pred= channel))
                                  s/LAST
                                  :subscriptions
                                  s/NIL->VECTOR
                                  s/END]
                                 [sub] s))))

(defn delete-subscription [state channel kp]
  (swap! state (fn [s] (s/setval [:sessions
                                  s/ALL
                                  (s/selected? s/FIRST (s/pred= channel))
                                  s/LAST
                                  :subscriptions
                                  (s/subselect [s/ALL (s/selected? (s/pred= kp))])
                                  s/FIRST]
                                 s/NONE s))))

(defn get-subscriptions [state]
  (s/select [:sessions
             s/ALL
             (s/collect-one s/FIRST)
             s/LAST
             :subscriptions]
            state))

(defn get-user-sessions [state uid]
  (s/select [:sessions
             s/ALL
             (s/selected? [s/LAST :user (s/pred= uid)])
             s/FIRST]
            @state))

(defn assoc-channel [state channel]
  (swap! state assoc-in [:sessions channel] {}))

(defn dissoc-channel [state channel]
  (swap! state update-in [:sessions] dissoc channel))
