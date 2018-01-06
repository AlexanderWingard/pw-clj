(ns clweb.util
  #?(:clj (require [org.httpkit.server :refer [send!]])))

(defn ws-send [channel msg]
  (if (instance? #?(:clj clojure.lang.Atom
                    :cljs cljs.core/Atom)
                 channel)
    (swap! channel conj msg)
    (when (not (nil? channel))
      #?(:clj (send! channel (pr-str msg))
         :cljs (.send channel (pr-str msg)))))
  msg)

(defn publish-to-all [channels message]
  (doseq [channel channels]
    (ws-send channel message)))

(defn deep-merge [v & vs]
  (letfn [(rec-merge [v1 v2]
            (if (and (map? v1) (map? v2))
              (merge-with deep-merge v1 v2)
              v2))]
    (when (some identity vs)
      (reduce #(rec-merge %1 %2) v vs))))

(defn keys-in [m]
  (if (map? m)
    (vec
     (mapcat (fn [[k v]]
               (let [sub (keys-in v)
                     nested (map #(into [k] %) (filter (comp not empty?) sub))]
                 (if (seq nested)
                   nested
                   [[k]])))
             m))
    []))

(defn action [state channel a]
  (ws-send channel (assoc @state :action a)))
