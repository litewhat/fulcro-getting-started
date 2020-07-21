(ns app.user-registration.model-test
  (:require [cljs.test :refer [deftest testing are is]]
            [app.user-registration.model :as sut]
            [app.test.mock :as mock]))

(deftest make-status-transition-test
  (are [status event next-status] (let [reg-before {:user-registration/id     (random-uuid)
                                                    :user-registration/status status}
                                        reg-after  (sut/make-status-transition reg-before event)]
                                    (= next-status (:user-registration/status reg-after)))
    ;; defined transitions
    :started :correct-input :valid-inputs
    :started :wrong-input :invalid-inputs
    :valid-inputs :wrong-input :invalid-inputs
    :valid-inputs :correct-input :valid-inputs
    :valid-inputs :click-register :in-progress
    :invalid-inputs :correct-input :valid-inputs
    :invalid-inputs :wrong-input :invalid-inputs
    :in-progress :success :success
    :in-progress :error :failure

    ;; undefined transitions
    :in-progress :wrong-input :in-progress
    :in-progress :correct-input :in-progress
    :success :correct-input :success
    :success :wrong-input :success
    :failure :correct-input :failure
    :failure :wrong-input :failure
    :failure :success :failure
    :failure :error :failure))

(deftest input-values-test
  (testing "completed registration inputs"
    (let [registration (mock/registration)]
      (= {:user-registration/email            "abc@test.com"
          :user-registration/password         "zaq1@WSX"
          :user-registration/confirm-password "zaq1@WSX"}
         (sut/input-values registration))))

  (testing "missing inputs"
    (let [registration (-> (mock/registration) (dissoc :user-registration/email))]
      (= {:user-registration/password         "zaq1@WSX"
          :user-registration/confirm-password "zaq1@WSX"}
         (sut/input-values registration)))

    (let [registration (-> (mock/registration) (dissoc :user-registration/password))]
      (= {:user-registration/email            "abc@test.com"
          :user-registration/confirm-password "zaq1@WSX"}
         (sut/input-values registration)))

    (let [registration (-> (mock/registration) (dissoc :user-registration/confirm-password))]
      (= {:user-registration/email    "abc@test.com"
          :user-registration/password "zaq1@WSX"}
         (sut/input-values registration)))))

(deftest split-affected-errors-test
  (testing "only affected errors"
    (let [pass-err         {:error/code       :invalid-password
                            :error/message    "Password should contain minimum 8 characters, lowercase letter, uppercase letter and number"
                            :error/field-name :user-registration/password}
          current-errors   [pass-err]
          data-to-validate {:user-registration/email            "abcdef"
                            :user-registration/password         "qwerty"
                            :user-registration/confirm-password "qwe"}]
      (is (= {:affected [pass-err]} (sut/split-affected-errors current-errors data-to-validate)))))

  (testing "only unaffected errors"
    (let [pass-err         {:error/code       :invalid-password
                            :error/message    "Password should contain minimum 8 characters, lowercase letter, uppercase letter and number"
                            :error/field-name :user-registration/password}
          current-errors   [pass-err]
          data-to-validate {:user-registration/email "abcdef"}]
      (is (= {:unaffected [pass-err]} (sut/split-affected-errors current-errors data-to-validate)))))

  (testing "unaffected errors exist"
    (let [pass-err         {:error/code       :invalid-password
                            :error/message    "Password should contain minimum 8 characters, lowercase letter, uppercase letter and number"
                            :error/field-name :user-registration/password}
          confirm-pass-err {:error/code       :invalid-confirm-password
                            :error/message    "Password does not match"
                            :error/field-name :user-registration/confirm-password}
          current-errors   [pass-err confirm-pass-err]
          data-to-validate {:user-registration/confirm-password "qwe"}]
      (is (= {:affected [confirm-pass-err] :unaffected [pass-err]}
             (sut/split-affected-errors current-errors data-to-validate))))))

(deftest by-id-test
  (testing "registration exists"
    (let [env          (mock/env)
          registration (sut/by-id env #uuid"f8c38951-9dda-448d-abd3-dcd5f0a918c1")]
      (is (= {:user-registration/id               #uuid"f8c38951-9dda-448d-abd3-dcd5f0a918c1"
              :user-registration/errors           []
              :user-registration/email            "test@example.com"
              :user-registration/status           :valid-inputs
              :user-registration/password         "zaq1@WSX"
              :user-registration/confirm-password "zaq1@WSX"}
             registration))))

  (testing "registration does not exist"
    (let [env          (mock/env)
          registration (sut/by-id env (random-uuid))]
      (is (nil? registration)))))

(deftest select-errors-test
  (testing "errors exist in error table"
    (let [env       (mock/env)
          error-ids [#uuid"2630743f-8d83-4e14-a577-3493dad4c802" #uuid"c5d4f45d-524c-4542-b702-4b1e02a62d69"]
          result    (sut/select-errors env [error-ids])]
      (= {#uuid"2630743f-8d83-4e14-a577-3493dad4c802"
          {:error/code       :invalid-email
           :error/message    "Invalid email"
           :error/field-name :user-registration/email
           :error/id         #uuid"2630743f-8d83-4e14-a577-3493dad4c802"}
          #uuid"c5d4f45d-524c-4542-b702-4b1e02a62d69"
          {:error/code       :invalid-confirm-password
           :error/message    "Password does not match"
           :error/field-name :user-registration/confirm-password
           :error/id         #uuid"c5d4f45d-524c-4542-b702-4b1e02a62d69"}}
         result)))

  (testing "errors do not exist"
    (let [env       (mock/env)
          error-ids [(random-uuid) (random-uuid)]
          result    (sut/select-errors env [error-ids])]
      (= {} result))))

(deftest list-errors-test
  (let [env    (mock/env)
        errors (sut/list-errors env #uuid"3f65cf59-4049-4ed4-be1e-f51cc93e331c")]
    (is (= 3 (count errors)))
    (is (= #{#uuid"2630743f-8d83-4e14-a577-3493dad4c802"
             #uuid"9b496605-9371-4c9e-9ab3-b7824e5af59f"
             #uuid"c5d4f45d-524c-4542-b702-4b1e02a62d69"}
           (set (map :error/id errors))))))
