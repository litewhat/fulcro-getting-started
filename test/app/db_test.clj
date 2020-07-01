(ns app.db-test
  (:require [clojure.test :refer [deftest testing is]]
            [app.db :as db]))

(deftest ^:integration db-connection-test
  (testing "connection with database can be established"
    (db/set-up-tables! db/conn-spec)
    ;list tables and check whether all exists
    (db/tear-down-tables! db/conn-spec)
    ;list tables and check whether all should be removed
    ))