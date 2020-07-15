(ns app.db-test
  (:require [clojure.test :refer [deftest testing is]]
            [hugsql.core :as hc]
            [app.db :as db]))

(defn list-table-names
  [db-conn]
  (map :tablename (hc/db-run db-conn "SELECT * FROM pg_catalog.pg_tables WHERE schemaname != 'pg_catalog' AND schemaname != 'information_schema';")))

(defn list-data-types
  [db-conn]
  (map :type (hc/db-run db-conn "SELECT n.nspname as schema, t.typname as type FROM pg_type t LEFT JOIN pg_catalog.pg_namespace n ON n.oid = t.typnamespace WHERE (t.typrelid = 0 OR (SELECT c.relkind = 'c' FROM pg_catalog.pg_class c WHERE c.oid = t.typrelid)) AND NOT EXISTS(SELECT 1 FROM pg_catalog.pg_type el WHERE el.oid = t.typelem AND el.typarray = t.oid) AND n.nspname NOT IN ('pg_catalog', 'information_schema');")))

(deftest ^:integration db-connection-test
  (testing "all tables and data types exist after setup"
    (db/set-up-tables! db/conn-spec)

    (let [tables (set (list-table-names db/conn-spec))]
      (is (= #{"person" "person_list" "person_list_people" "app_user" "token"} tables)))

    (let [data-types (set (list-data-types db/conn-spec))]
      (is (= #{"token_type"} data-types))))

  (testing "no table and data types exists after tear down"
    (db/tear-down-tables! db/conn-spec)

    (let [tables (set (list-table-names db/conn-spec))]
      (is (empty? tables)))

    (let [data-types (set (list-data-types db/conn-spec))]
      (is (empty? data-types)))))
