(ns app.parser-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [app.db :as db]
            [app.parser :as sut]
            [app.test.fixtures :as fixtures]
            [app.user.db.queries :as user-queries]))

(use-fixtures :each (fixtures/db db/conn-spec))

(deftest ^:integration register-test
  (testing "when user registered successfuly"
    (let [email "testregister1@email.com"
          users-before   (count (user-queries/get-all-app-users db/conn-spec))
          mutation-input {:user/email            email
                          :user/password         "zaq1@WSX"
                          :user/confirm-password "zaq1@WSX"}
          response (-> (sut/api-parser `[(app.user-registration.mutations/register ~mutation-input)])
                       (get 'app.user-registration.mutations/register))]

      (testing "returns user map"
        (is (s/valid? :app.user-registration.mutations/register.response response))
        (is (= email (:user/email response)))
        (is (= java.sql.Timestamp (type (:user/created-at response)))))

      (testing "creates user in database"
        (let [users-after (count (user-queries/get-all-app-users db/conn-spec))
              new-user (user-queries/get-app-user-by-email db/conn-spec {:email email})]
          (is (= (inc users-before) users-after))
          (is (= email (:email new-user)))
          (is (= (:user/id response) (:id new-user)))
          (is (= (:user/created-at response) (:created_at new-user)))))))

  (testing "when invalid data provided"
    (let [email "invalid-email"
          users-before   (count (user-queries/get-all-app-users db/conn-spec))
          mutation-input {:user/email            email
                          :user/password         "invalid-pass"
                          :user/confirm-password "pass"}
          response (-> (sut/api-parser `[(app.user-registration.mutations/register ~mutation-input)])
                       (get 'app.user-registration.mutations/register))]

      (testing "returns errors map"
        (is (s/valid? :app.user-registration.mutations/register.response response))
        (let [errors (-> response :errors set)]
         (is (= (set [{:code    :invalid-email
                       :message "Invalid email"
                       :param   :user/email}
                      {:code    :invalid-password
                       :message "Password should contain minimum 8 characters, lowercase letter, uppercase letter and number"
                       :param   :user/password}
                      {:code    :invalid-confirm-password
                       :message "Password does not match"
                       :param   :user/confirm-password}])
                errors))))

      (testing "does not create user in database"
        (let [users-after (count (user-queries/get-all-app-users db/conn-spec))
              user (user-queries/get-app-user-by-email db/conn-spec {:email email})]
          (is (= users-before users-after))
          (is (nil? user)))))))
