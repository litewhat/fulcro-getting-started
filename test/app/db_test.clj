(ns app.db-test
  (:require [clojure.test :refer [deftest testing is]]
            [hugsql.core :as hc]
            [app.db :as db]
            [app.db.queries :as q]))

(defn list-table-names
  [db-conn]
  (map :tablename (q/get-all-tables db-conn)))

(defn list-data-types
  [db-conn]
  (map :type (q/get-all-data-types db-conn)))

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
