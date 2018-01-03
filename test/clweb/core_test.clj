(ns clweb.core-test
  (:require
   [clweb.core :as clweb]
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
    (is (= s (util/hashify m)))
    (is (= m (util/unhashify s)))))


(deftest registration-test
  (testing "faulty registration"
    (let [fe-state {:registration-form
                    {:username {:value "test"}
                     :password-1 {:value "pass"}}}
          expected-pass {:registration-form {:password-2 {:error nil}}}
          expected-fail {:registration-form {:password-2 {:error "Passwords don't match"}}}]
      (is (= expected-pass (clweb/validate-registration (assoc-in fe-state [:registration-form :password-2 :value] "pass"))))
      (is (= expected-fail (clweb/validate-registration (assoc-in fe-state [:registration-form :password-2 :value] "fail")))))))
