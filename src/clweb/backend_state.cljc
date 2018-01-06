(ns clweb.backend-state)

(defonce sessions (atom {}))
(defonce state (atom {}))

(defn register-user [channel username password]
  (let [uid username]
    (swap! state update-in [:users]
           assoc uid {:username username :password password})
    (swap! sessions
           assoc-in [channel :user] uid)
    uid))

(defn assoc-channel [channel]
  (swap! sessions assoc channel {}))

(defn dissoc-channel [channel]
  (swap! sessions dissoc channel))
