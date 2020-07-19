(ns app.user-registration.model)

(defn list-errors
  "Returns errors associated with given registration id."
  [{:keys [:state] :as env} id]
  (let [state-map    (deref state)
        registration (get-in state-map [:user-registration/id id])
        error-ids    (map second (:user-registration/errors registration))
        error-table  (:error/id state-map)]
    (vals (select-keys error-table error-ids))))

(def status-sm
  {:started        {:correct-input :valid-inputs
                    :wrong-input   :invalid-inputs}
   :valid-inputs   {:click-register :in-progres
                    :wrong-input    :invalid-inputs}
   :invalid-inputs {:correct-input :valid-inputs}
   :in-progress    {:success :success
                    :error   :failure}
   :success        nil
   :failure        nil})

(defn make-status-transition
  [{:keys [:state] :as env} id event]
  (let [state-map (deref state)
        registration (get-in state-map [:user-registration/id id])]
    (update registration :user-registration/status #(or (get (get status-sm %) event) %))))
