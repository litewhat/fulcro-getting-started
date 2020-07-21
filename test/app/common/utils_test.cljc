(ns app.common.utils-test
  (:require #?(:clj  [clojure.test :refer [deftest are is testing]]
               :cljs [cljs.test :refer [deftest are is testing]])
            [app.common.utils :as sut]))

(deftest has-lowercase-letter?-test
  (are [pred s] (pred (sut/has-lowercase-letter? s))
    true? "1Óść"
    true? "ąółć"
    true? "abcd"
    false? "1OŚĆ"
    false? "1234"
    false? ""))

(deftest has-uppercase-letter?-test
  (are [pred s] (pred (sut/has-uppercase-letter? s))
    true? "1Óść"
    false? "ąółć"
    false? "abcd"
    true? "1OŚĆ"
    true? " Oo1Ó"
    false? "1234"
    false? ""))

(deftest has-number?-test
  (are [pred s] (pred (sut/has-number? s))
    true? "1Óść"
    false? "ąółć"
    false? "abcd"
    true? "1OŚĆ"
    true? " Oo1Ó"
    true? "1234"
    false? ""))