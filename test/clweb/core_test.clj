(ns clweb.core-test
  (:require
   [clweb.core :as clweb]
   [clweb.location-hash :as hash]
   [clweb.components.registration :as registration]
   [clweb.form-util :as form-util]
   [clweb.util :as util]
   [com.rpl.specter :as s]
   [clojure.test :refer :all]))

(deftest validation-test
  (testing "Passwords match"
    (let [data {:password-1 {:value "apa"}
                :password-2 {:value "bepa"}}
          expected {:password-1 {:value "apa"}
                    :password-2 {:value "bepa" :error "Passwords don't match"}}
          result (registration/validate data)]
      (is (= true (form-util/errors result))))))

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
