(ns app.person.model
  (:require [app.db :as db]
            [app.person.db.queries :as q]))

(defn- db-record->person
  [{:keys [id name age created_at]}]
  {:person/id         id
   :person/name       name
   :person/age        age
   :person/created-at created_at})

(defn- db-record->person-list
  [{:keys [id created_at]}]
  {:list/id         (read-string id)
   :list/label      id
   :list/created-at created_at})

(defn person-by-id
  [system id]
  (let [db-conn (get-in system [:db :conn])
        record  (q/get-person-by-id db-conn {:id id})]
    (db-record->person record)))

(defn all-people
  [system]
  (let [db-conn (get-in system [:db :conn])
        records (q/get-all-people db-conn)]
    (map db-record->person records)))

(defn person-list-by-id
  [system id]
  (let [db-conn (get-in system [:db :conn])]
    (when-some [xlist (db-record->person-list (q/get-person-list-by-id db-conn {:id (str id)}))]
      (let [people (mapv (fn [{:keys [person_id]}] {:person/id person_id})
                         (q/get-people-by-list-id db-conn {:list_id (str id)}))]
        (assoc xlist :list/people people)))))

(defn remove-person-from-list
  [system {list-id :list/id person-id :person/id}]
  (let [db-conn (get-in system [:db :conn])]
    (q/remove-person-from-list db-conn {:list_id (str list-id) :person_id person-id})
    (person-list-by-id system list-id)))

(comment
  (db-record->person (q/get-person-by-id db/conn-spec {:id 1}))
  (map db-record->person (q/get-all-people db/conn-spec))

  (db-record->person-list (q/get-person-list-by-id db/conn-spec {:id (str :friends)}))
  (mapv :person_id (q/get-people-by-list-id db/conn-spec {:list_id (str :friends)})))