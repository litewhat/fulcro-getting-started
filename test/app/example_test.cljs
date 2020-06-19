(ns app.example-test
  (:require [clojure.string :as str]
            [cljs.test :refer [deftest is testing]]))

(deftest example-test
  (testing "numbers-quality"
    (let [a 4]
     (is (= 5 (inc a)))))

  (testing "splitting string"
    (is (= ["a" "b" "c"] (filterv #(not= "" %) (str/split "abc" #""))))))