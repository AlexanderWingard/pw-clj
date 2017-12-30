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
