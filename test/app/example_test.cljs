(ns app.example-test
  (:require [cljs.test :refer [deftest is testing run-tests]]))

(deftest example-test
  (testing "numbers-quality"
    (is (= 2 4))))