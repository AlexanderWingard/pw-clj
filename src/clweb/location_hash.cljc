(ns clweb.location-hash
  (:require
   [clojure.string :as str]))

(defn hashify [m]
  (str "#" (str/join "/" (map #(str/join "/" %) (into [] m)))))

(defn unhashify [s]
  (apply hash-map (filter #(not= "" %) (str/split (subs s 1) #"/"))))
