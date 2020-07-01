(ns app.db-test
  (:require [clojure.test :refer [deftest testing is]]
            [hugsql.core :as hc]
            [app.db :as db]))

(defn list-table-names
  [db-conn]
  (map :tablename (hc/db-run db-conn "SELECT * FROM pg_catalog.pg_tables WHERE schemaname != 'pg_catalog' AND schemaname != 'information_schema';")))

(deftest ^:integration db-connection-test
  (testing "all tables exist after setup"
    (db/set-up-tables! db/conn-spec)
    (let [tables (set (list-table-names db/conn-spec))]
      (is (= #{"person" "person_list" "person_list_people"} tables))))

  (testing "no table exists after tear down"
    (db/tear-down-tables! db/conn-spec)
    (let [tables (set (list-table-names db/conn-spec))]
      (is (empty? tables)))))
