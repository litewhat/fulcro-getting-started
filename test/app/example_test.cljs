(ns app.example-test
  (:require [clojure.string :as str]
            [cljs.test :refer [deftest is testing]]
            [app.client]))

(deftest example-test
  (testing "numbers-quality"
    (let [a 4]
     (is (= 5 (inc a)))))

  (testing "splitting string"
    (is (= ["a" "b" "c"] (filterv #(not= "" %) (str/split "abc" #""))))))

(deftest dom-test
  (testing "root component"
    (let [selector "#app .container:first-child h1:first-child"]
      (is (= "Root component" (.-textContent (.querySelector js/document selector)))))))
