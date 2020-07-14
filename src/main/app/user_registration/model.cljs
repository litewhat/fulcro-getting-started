(ns app.user-registration.model)

(defn list-errors
  "Returns errors associated with given registration id."
  [{:keys [:state] :as env} id]
  (let [state-map    (deref state)
        registration (get-in state-map [:user-registration/id id])
        error-ids    (map second (:user-registration/errors registration))
        error-table  (:error/id state-map)]
    (vals (select-keys error-table error-ids))))
