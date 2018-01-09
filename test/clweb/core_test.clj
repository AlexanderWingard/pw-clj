(ns clweb.core-test
  (:require
   [clweb.core :as clweb]
   [clweb.location-hash :as hash]
   [clweb.components.registration :as registration]
   [clweb.form-util :as form-util]
   [clweb.util :as util]
   [com.rpl.specter :as s]
   [clojure.test :refer :all]
   [clweb.backend-state :as bes]))

(deftest deep-merge-test
  (testing "a deep merge"
    (let [a {:a {:b 10
                 :c 20
                 :d 0}
             :x "y"}
          b {:a {:d 30}}
          expected {:a {:b 10
                        :c 20
                        :d 30}
                    :x "y"}]
      (is (= expected (util/deep-merge a b) )))))

(deftest hashify-test
  (testing "hashification")
  (let [s "#page/test/apa/bepa"
        m {"page" "test" "apa" "bepa"}]
    (is (= s (hash/hashify m)))
    (is (= m (hash/unhashify s)))))

(deftest specter-subs
  (testing "some specter"
    (let [s (atom {})]
      (bes/assoc-channel s "channel1")
      (bes/add-subscription s "channel1" {:keypath [:apa]})
      (is (= {:sessions {"channel1" {:subscriptions [{:keypath [:apa]}]}}} @s))
      (bes/add-subscription s "channel1" {:keypath [:apa]})
      (bes/delete-subscription s "channel1" [:apa])
      (bes/delete-subscription s "channel1" [:apa])
      (is (= {:sessions {"channel1" {:subscriptions []}}} @s)))))
