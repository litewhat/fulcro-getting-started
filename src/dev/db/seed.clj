(ns db.seed
  (:require [taoensso.timbre :as log]
            [app.db :as db]
            [app.person.db.queries :as person-queries]
            [app.user.db.queries :as user-queries]))

(def people
  [["Paweł" 28]
   ["Andrea" 25]
   ["Josh" 34]
   ["Richard" 60]
   ["Eduardo" 26]
   ["Michael" 64]
   ["Sarah" 18]
   ["Jacob" 16]
   ["Marc" 24]
   ["Serge" 78]
   ["Luc" 42]
   ["Andy" 14]])

(def users
  [["seed1@user.com"]
   ["seed2@user.com"]
   ["seed3@user.com"]])

(def person-lists
  (map (comp vector str) [:friends :enemies]))

(def person-list-people
  {:friends #{1 3 5}
   :enemies #{2 4 6}})

(defn seed! [db-conn]
  (person-queries/batch-insert-person db-conn {:people people})
  (log/debug "Seeded 'person' table")
  (person-queries/batch-insert-person-list db-conn {:person_lists person-lists})
  (log/debug "Seeded 'person_list' table")
  (doseq [[list-id people-ids] person-list-people]
    (person-queries/add-people-to-list
      db-conn
      {:people (mapv (partial vector (str list-id)) people-ids)}))
  (log/debug "Seeded 'person_list_people' table")
  (user-queries/batch-insert-app-user db-conn {:users users})
  (log/debug "Seeded 'app_user' table")
  )

(comment
  (seed! db/conn-spec))