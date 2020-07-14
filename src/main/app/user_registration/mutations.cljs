(ns app.user-registration.mutations
  (:require [cljs.spec.alpha :as s]
            [com.fulcrologic.fulcro.mutations :refer [defmutation]]
            [app.user-registration.model.validation :as v]
            [taoensso.timbre :as log]
            [cljs.spec.test.alpha :as stest]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]))

;; update form data

(defmutation update-data
  [{registration-id :user-registration/id :as params}]
  (action [{:keys [state]}]
    (let [data-to-update (select-keys params [:user-registration/email :user-registration/password :user-registration/confirm-password])]
      (log/spy :debug "UPDATING USER REGISTRATION DATA")
      (swap! state #(update-in % [:user-registration/id registration-id] merge data-to-update)))))

;; validation

(defmutation validate-data
  [{registration-id :user-registration/id :as params}]
  (action [{:keys [component state] :as env}]
    (log/spy :debug "VALIDATING USER REGISTRATION DATA")
    (let [old-state              (deref state)
          field-names            [:user-registration/email :user-registration/password :user-registration/confirm-password]
          data-to-validate       (select-keys params field-names)
          affected-fields        (set (keys data-to-validate))
          validation-errors      (keep #(v/validate-input env (first %) (second %)) data-to-validate)
          registration           (get-in old-state [:user-registration/id registration-id])
          registration-error-ids (map second (:user-registration/errors registration))
          error-table            (:error/id old-state)
          registration-errors    (vals (select-keys error-table registration-error-ids))
          {:keys [affected unaffected]} (group-by (fn [item]
                                                    (if (contains? affected-fields (:error/field-name item))
                                                      :affected
                                                      :unaffected))
                                                  registration-errors)
          new-errors             (concat unaffected (map #(assoc % :error/id (random-uuid)) validation-errors))]
      (swap! state (fn [s]
                     (cond-> s
                       true (merge/merge-component component {:user-registration/id     registration-id
                                                              :user-registration/errors (vec new-errors)})
                       (seq affected) (assoc :error/id (apply dissoc error-table (map :error/id affected)))))))))


;; empty errors
{:user-registration/id     #uuid"fcf5f139-7919-46e6-9d75-6d44f9df32db"
 :user-registration/errors []}

;; error exist
;; Write tests
(comment
  (let [registration-id  (random-uuid)
        email-error-uuid (random-uuid)
        psswd-error-uuid (random-uuid)
        params           {:user-registration/id registration-id :user-registration/email "asdadsasda"}
        state            {:error/id {email-error-uuid {:error/id         email-error-uuid
                                                       :error/code       :invalid-email
                                                       :error/message    "Invalid email"
                                                       :error/field-name :user-registration/email}
                                     psswd-error-uuid {:error/id         psswd-error-uuid
                                                       :error/code       :invalid-password
                                                       :error/message    "Invalid password"
                                                       :error/field-name :user-registration/password}}
                          :user-registration/id
                                    {registration-id
                                     {:user-registration/confirm-password "asdasdasd"
                                      :user-registration/email            "asdasdasd"
                                      :user-registration/errors           [[:error/id email-error-uuid] [:error/id psswd-error-uuid]]
                                      :user-registration/id               registration-id
                                      :user-registration/password         "asdasd"}}}
        env              {:state state}]
    (let [field-names            [:user-registration/email :user-registration/password :user-registration/confirm-password]
          data-to-validate       (select-keys params field-names)
          validation-errors      (map #(v/validate-input env (first %) (second %)) data-to-validate)
          registration           (get-in state [:user-registration/id registration-id])
          registration-error-ids (map second (:user-registration/errors registration))
          registration-errors    (vals (select-keys (:error/id state) registration-error-ids))
          affected-fields        (set (map :error/field-name validation-errors))
          [_ unaffected] (split-with #(contains? affected-fields (:error/field-name %)) registration-errors)
          new-errors             (concat unaffected (map #(assoc % :error/id (random-uuid)) validation-errors))]
      (merge/merge-component! (vec new-errors)))))
