(ns app.user-registration.model.validation-test
  (:require [clojure.test :refer [deftest are is testing]]
            [app.user-registration.model.validation :as sut]
            [app.test.mock :as mock]))

(deftest validate-data-test
  (testing "when email invalid"
    (let [env (-> (mock/env)
                  (assoc ::sut/registration-id #uuid"f8c38951-9dda-448d-abd3-dcd5f0a918c1")
                  (assoc ::sut/data {:user-registration/email "invalid-email"}))]
      (is (= '({:error/code       :invalid-email,
                :error/message    "Invalid email",
                :error/field-name :user-registration/email})
             (sut/validate-data env)))))

  (testing "when email valid"
    (let [env (-> (mock/env)
                  (assoc ::sut/registration-id #uuid"f8c38951-9dda-448d-abd3-dcd5f0a918c1")
                  (assoc ::sut/data {:user-registration/email "test@example.com"}))]
      (is (empty? (sut/validate-data env)))))

  (testing "when password invalid"
    (let [env (-> (mock/env)
                  (assoc ::sut/registration-id #uuid"f8c38951-9dda-448d-abd3-dcd5f0a918c1")
                  (assoc ::sut/data {:user-registration/password "qwerty"}))]
      (is (= '({:error/code       :invalid-password
                :error/message    "Password should contain minimum 8 characters, lowercase letter, uppercase letter and number"
                :error/field-name :user-registration/password})
             (sut/validate-data env)))))

  (testing "when password valid"
    (let [env (-> (mock/env)
                  (assoc ::sut/registration-id #uuid"f8c38951-9dda-448d-abd3-dcd5f0a918c1")
                  (assoc ::sut/data {:user-registration/password "zaq1@WSX"}))]
      (is (empty? (sut/validate-data env)))))

  (testing "when confirmed password does not match"
    (let [env (-> (mock/env)
                  (assoc ::sut/registration-id #uuid"f8c38951-9dda-448d-abd3-dcd5f0a918c1")
                  (assoc ::sut/data {:user-registration/confirm-password "qwerty"}))]
      (is (= '({:error/code       :invalid-confirm-password
                :error/message    "Password does not match"
                :error/field-name :user-registration/confirm-password})
             (sut/validate-data env)))))

  (testing "when confirmed password matches"
    (let [env (-> (mock/env)
                  (assoc ::sut/registration-id #uuid"f8c38951-9dda-448d-abd3-dcd5f0a918c1")
                  (assoc ::sut/data {:user-registration/confirm-password "zaq1@WSX"}))]
      (is (empty? (sut/validate-data env))))))
