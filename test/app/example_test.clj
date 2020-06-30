(ns app.example-test
  (:require [clojure.test :refer :all]))

(deftest fail-test
  (testing "failing"
    (is (= 2 1))))