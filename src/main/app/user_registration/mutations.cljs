(ns app.user-registration.mutations
  (:require [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [com.fulcrologic.fulcro.mutations :refer [defmutation]]
            [taoensso.timbre :as log]
            [app.user-registration.model.validation :as v]
            [app.user-registration.model :as m]))

(defmutation update-input
  [{registration-id :user-registration/id :as params}]
  (action [{:keys [state]}]
    (let [data-to-update (select-keys params [:user-registration/email :user-registration/password :user-registration/confirm-password])]
      (swap! state #(update-in % [:user-registration/id registration-id] merge data-to-update)))))

(defn split-affected-errors
  "Takes current registration errors, presumably returned by
  `app.user-registration.model/list-errors` function and `data`
  map which is supposed to be validated
  i.e. by `app.user-registration.model.validation/validate-data` function.

   Groups registration errors into two groups:
   1. `:affected` by current registration process
      - these errors should be overwritten in app state
   2. `:unaffected` by current registration process
      - these errors should remain unchanged in app state."
  [registration-errors data]
  (let [affected-fields     (set (keys data))]
   (group-by (fn [item]
               (if (contains? affected-fields (:error/field-name item))
                 :affected
                 :unaffected))
             registration-errors)))

(defmutation validate-input
  [{registration-id :user-registration/id :as params}]
  (action [{:keys [component state] :as env}]
    (let [state-map           (deref state)
          field-names         [:user-registration/email :user-registration/password :user-registration/confirm-password]
          data-to-validate    (select-keys params field-names)
          env'                (assoc env ::v/registration-id registration-id ::v/data data-to-validate)
          validation-errors   (v/validate-data env')
          registration-errors (m/list-errors env' registration-id)
          {:keys [affected unaffected]} (split-affected-errors registration-errors data-to-validate)
          component-data      {:user-registration/id     registration-id
                               :user-registration/errors (vec (concat unaffected (map #(assoc % :error/id (random-uuid)) validation-errors)))}]
      (swap! state (fn [s]
                     (cond-> s
                       (seq affected) (assoc :error/id (apply dissoc (:error/id state-map) (map :error/id affected)))
                       :always (merge/merge-component component component-data)))))))

(defmutation register
  [{:user/keys [:email :password :confirm-password] :as params}]
  (action [{:keys [state] :as env}]
    (log/debug "Register user")
    (log/spy :debug params))
  (remote [env]
    (log/debug "Calling remote register mutation")
    true))
