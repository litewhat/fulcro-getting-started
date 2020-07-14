(ns app.user-registration.mutations
  (:require [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [com.fulcrologic.fulcro.mutations :refer [defmutation]]
            [taoensso.timbre :as log]
            [app.user-registration.model.validation :as v]))

(defmutation update-data
  [{registration-id :user-registration/id :as params}]
  (action [{:keys [state]}]
    (let [data-to-update (select-keys params [:user-registration/email :user-registration/password :user-registration/confirm-password])]
      (swap! state #(update-in % [:user-registration/id registration-id] merge data-to-update)))))

(defmutation validate-data
  [{registration-id :user-registration/id :as params}]
  (action [{:keys [component state] :as env}]
    (let [state-map              (deref state)
          field-names            [:user-registration/email :user-registration/password :user-registration/confirm-password]
          data-to-validate       (select-keys params field-names)
          validation-errors      (keep #(v/validate-input (assoc env ::v/registration-id registration-id)
                                                          (first %)
                                                          (second %))
                                       data-to-validate)
          registration           (get-in state-map [:user-registration/id registration-id])
          registration-error-ids (map second (:user-registration/errors registration))
          error-table            (:error/id state-map)
          registration-errors    (vals (select-keys error-table registration-error-ids))
          affected-fields        (set (keys data-to-validate))
          {:keys [affected unaffected]} (group-by (fn [item]
                                                    (if (contains? affected-fields (:error/field-name item))
                                                      :affected
                                                      :unaffected))
                                                  registration-errors)
          new-errors             (concat unaffected (map #(assoc % :error/id (random-uuid)) validation-errors))]
      (swap! state (fn [s]
                     (cond-> s
                       (seq affected) (assoc :error/id (apply dissoc error-table (map :error/id affected)))
                       :always (merge/merge-component component {:user-registration/id     registration-id
                                                                 :user-registration/errors (vec new-errors)})))))))

(defmutation register
  [{:user/keys [email password] :as params}]
  (action [{:keys [state] :as env}]
    (log/debug "Register user")
    (log/spy :debug params))
  #_(remote [env] true))
