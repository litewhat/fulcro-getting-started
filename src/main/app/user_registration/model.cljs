(ns app.user-registration.model)

(def input-field-names
  [:user-registration/email
   :user-registration/password
   :user-registration/confirm-password])

(def status-sm
  {:started        {:correct-input :valid-inputs
                    :wrong-input   :invalid-inputs}
   :valid-inputs   {:click-register :in-progress
                    :wrong-input    :invalid-inputs}
   :invalid-inputs {:correct-input :valid-inputs}
   :in-progress    {:success :success
                    :error   :failure}
   :success        nil
   :failure        nil})

(defn make-status-transition
  [registration event]
  (update registration :user-registration/status #(or (get (get status-sm %) event) %)))

(defn input-values
  [registration]
  (select-keys registration input-field-names))

(defn split-affected-errors
  "Takes current registration errors (before performing validation) returned by
  `app.user-registration.model/list-errors` function and `data` map which is
  supposed to be passed to a validation function.

   Groups registration errors into two groups:
   1. `:affected` - these errors should be overwritten in app state
   2. `:unaffected` - these errors should remain unchanged in app state.

  Validation function - see `app.user-registration.model.validation/validate-data`."
  [registration-errors data]
  (let [affected-fields (set (keys data))]
    (group-by (fn [item]
                (if (contains? affected-fields (:error/field-name item))
                  :affected
                  :unaffected))
              registration-errors)))

(defn by-id
  "Returns registration with given id"
  [{:keys [state] :as env} id]
  (let [state-map (deref state)]
    (get-in state-map [:user-registration/id id])))

(defn select-errors
  "Selects errors with given ids from error table.
  Result:
  {error-id-1 error-1
   error-id-2 error-2
   ...}"
  [{:keys [state] :as env} ids]
  (let [state-map   (deref state)
        error-table (:error/id state-map)]
    (vals (select-keys error-table ids))))

(defn list-errors
  "Returns errors associated with given registration id."
  [env id]
  (let [registration (by-id env id)
        error-ids    (map second (:user-registration/errors registration))]
    (select-errors env error-ids)))