(ns clweb.core-test
  (:require [cljs.test
             :refer-macros [deftest
                            is
                            testing
                            run-tests]]))

(deftest test-the-truth
  (is (= true (not true))))
(enable-console-print!)
(cljs.test/run-tests)
