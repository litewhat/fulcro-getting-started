(ns db.seed
  (:require [app.db :as db]
            [app.person.db.queries :as person-queries]))

(def people
  [["Paweł" 28]
   ["John" 28]
   ["Julia" 25]
   ["Josh" 32]
   ["Anthony" 30]
   ["Lucian" 65]
   ["Rebecca" 46]
   ["Tom" 55]
   ["Adam" 40]
   ["Eve" 40]])

(def person-lists
  (map (comp vector str) [:friends :enemies]))

(def person-list-people
  {:friends #{1 3 5 7}
   :enemies #{2 4 6 8}})

(defn seed! [db-conn]
  (person-queries/batch-insert-person db-conn {:people people})
  (person-queries/batch-insert-person-list db-conn {:person_lists person-lists})
  (doseq [[list-id people-ids] person-list-people]
    (person-queries/add-people-to-list
      db-conn
      {:people (mapv (partial vector (str list-id)) people-ids)})))

(comment
  (seed! db/conn-spec))