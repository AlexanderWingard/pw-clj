(ns clweb.core-test
  (:require
   [clweb.core :as clweb]
   [clweb.location-hash :as hash]
   [clweb.util :as util]
   [com.rpl.specter :as s]
   [clojure.test :refer :all]))

(deftest validation-test
  (testing "Passwords match"
    (let [data {:password-1 {:value "apa"}
                :password-2 {:value "bepa"}}
          expected {:password-1 {:value "apa"}
                    :password-2 {:value "bepa" :error "Passwords don't match"}}
          actual (s/transform [(s/collect-one :password-1 :value)
                               (s/collect-one :password-2 :value)
                               :password-2]
                              (fn [pw1 pw2 x]
                                (conj x (when (not= pw1 pw2)
                                          [:error "Passwords don't match"])))
                              data)]
      (is (= actual expected)))))

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
