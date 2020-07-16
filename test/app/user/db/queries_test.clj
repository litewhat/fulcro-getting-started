(ns app.user.db.queries-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [app.db :as db]
            [app.db.queries :as db-queries]
            [app.user.db.queries :as sut]
            [app.test.fixtures :as fixtures]
            [db.seed :as dbs])
  (:import [java.util UUID]))

(use-fixtures :each (fixtures/db db/conn-spec))

(deftest ^:integration add-token-type-test
  (sut/add-token-type db/conn-spec {:value "id"})
  (let [token-types (db-queries/get-all-enum-type-values db/conn-spec {:name "token_type"})]
    (is (= 3 (count token-types)))
    (is (= #{"access" "refresh" "id"} (set (map :enum_value token-types))))
    (is (= #{"token_type"} (set (map :enum_name token-types))))))

(deftest ^:integration insert-app-user-test
  (testing "successful insert"
    (let [emails    ["test1@example.com" "test2@example.com"]
          users     (for [email emails]
                      (sut/insert-app-user db/conn-spec {:email email}))
          creations (map :created_at users)]
      (is (= emails (mapv :email users)))
      (is (every? nil? (map :deleted_at users)))
      (is (.before (first creations) (second creations)))))

  (testing "email duplication"
    (is (thrown-with-msg?
          org.postgresql.util.PSQLException
          #"ERROR: duplicate key value violates unique constraint \"app_user_email_key\""
          (sut/insert-app-user db/conn-spec {:email "test1@example.com"})))))

(deftest ^:integration batch-insert-app-user-test
  (testing "successful insert"
    (let [emails   ["batchtest1@example.com" "batchtest2@example.com" "batchtest3@example.com"]
          response (sut/batch-insert-app-user db/conn-spec {:users (mapv vector emails)})
          inserted (for [email emails] (sut/get-app-user-by-email db/conn-spec {:email email}))]
      (is (= (count emails) (count response)))
      (is (= (set emails) (set (mapv :email inserted))))
      (is (every? nil? (map :deleted_at inserted)))
      (is (= 1 (count (set (map :created_at inserted)))))))

  (testing "email duplication"
    (let [emails ["batchtest1@example.com" "batchtest4@example.com" "batchtest2@example.com"]]
      (is (thrown-with-msg?
            org.postgresql.util.PSQLException
            #"ERROR: duplicate key value violates unique constraint \"app_user_email_key\""
            (sut/batch-insert-app-user db/conn-spec {:users (mapv vector emails)}))))))

(deftest ^:integration get-app-user-by-id-test
  (testing "get inserted user by id"
    (let [email    "inserttest1@example.com"
          inserted (sut/insert-app-user db/conn-spec {:email email})
          res      (sut/get-app-user-by-id db/conn-spec {:id (:id inserted)})]
      (is (= email (:email res)))
      (is (nil? (:deleted_at res)))
      (is (= #{:id :email :created_at :deleted_at} (set (keys res))))))

  (testing "get user by id when does not exist"
    (let [res (sut/get-app-user-by-id db/conn-spec {:id (UUID/randomUUID)})]
      (is (nil? res)))))

(deftest ^:integration get-app-user-by-email-test
  (let [emails ["seed1@user.com" "seed2@user.com" "seed3@user.com"]
        users  (for [email emails]
                 (sut/get-app-user-by-email db/conn-spec {:email email}))
        params (set (map (comp set keys) users))]
    (is (= (count emails) (count (keep identity users))))
    (is (= 1 (count params)))
    (is (= #{:id :email :created_at :deleted_at} (first params))))
  )

(deftest ^:integration get-all-app-users-test
  (let [users  (sut/get-all-app-users db/conn-spec)
        params (set (map (comp set keys) users))]
    (is (= (count dbs/users) (count users)))
    (is (= #{:id :email :created_at :deleted_at} (first params)))))

(deftest ^:integration get-app-users-by-emails-test
  (testing "existing users"
    (let [emails ["seed1@user.com" "seed2@user.com" "seed3@user.com"]
          users  (sut/get-app-users-by-emails db/conn-spec {:emails emails})
          params (set (map (comp set keys) users))]
      (is (= 1 (count params)))
      (is (= #{:id :email :created_at :deleted_at} (first params)))
      (is (count emails) (count users))))

  (testing "nonexistent users"
    (let [emails ["nonexistent1@user.com" "nonexistent2@user.com" "nonexistent3@user.com"]
          users  (sut/get-app-users-by-emails db/conn-spec {:emails emails})]
      (is (zero? (count users))))))

(deftest ^:integration delete-app-user-test
  (let [user     (sut/get-app-user-by-email db/conn-spec {:email "seed1@user.com"})
        affected (sut/delete-app-user db/conn-spec {:id (:id user)})]
    (is (= 1 affected))))

(deftest ^:integration batch-delete-app-user-test
  (testing "delete existing users"
    (let [emails       ["seed1@user.com" "seed2@user.com" "seed3@user.com"]
          users-before (for [email emails] (sut/get-app-user-by-email db/conn-spec {:email email}))
          affected     (sut/batch-delete-app-user db/conn-spec {:ids (map :id users-before)})
          users-after  (sut/get-all-app-users db/conn-spec)]
      (is (= (count emails) affected))
      (is (= affected (- (count users-before) (count users-after))))))

  (testing "delete nonexistent users"
    (let [ids          (repeatedly 2 #(UUID/randomUUID))
          users-before (sut/get-all-app-users db/conn-spec)
          affected     (sut/batch-delete-app-user db/conn-spec {:ids (vec ids)})
          users-after  (sut/get-all-app-users db/conn-spec)]
      (is (= (count users-before) (count users-after))))))

(deftest ^:integration mark-deleted-app-user-test
  (testing "marking existing users as deleted"
    (let [emails    ["seed1@user.com" "seed2@user.com" "seed3@user.com"]
          users     (for [email emails] (sut/get-app-user-by-email db/conn-spec {:email email}))
          res       (for [user users] (sut/mark-deleted-app-user db/conn-spec {:id (:id user)}))
          deletions (map :deleted_at res)]
      (is (= (count res) (count users) (count emails)))
      (is (every? nil? (map :deleted_at users)))
      (is (every? #(= java.sql.Timestamp (type %)) deletions))
      (is (= deletions (sort #(.before %1 %2) deletions)))))

  (testing "marking nonexistent users as deleted"
    (let [ids          (repeatedly 2 #(UUID/randomUUID))
          users-before (sut/get-all-app-users db/conn-spec)
          responses    (for [id ids] (sut/mark-deleted-app-user db/conn-spec {:id id}))
          affected     (count (keep identity responses))
          users-after  (sut/get-all-app-users db/conn-spec)]
      (is (zero? affected))
      (is (= (count users-before) (count users-after)))
      (is (every? nil? responses)))))

(deftest ^:integration batch-mark-deleted-app-user-test
  (testing "marking existing users as deleted"
    (let [emails          ["seed1@user.com" "seed2@user.com" "seed3@user.com"]
          users-to-delete (for [email emails] (sut/get-app-user-by-email db/conn-spec {:email email}))
          users-before    (sut/get-all-app-users db/conn-spec)
          response        (sut/batch-mark-deleted-app-user db/conn-spec {:ids (mapv :id users-before)})
          users-after     (sut/get-all-app-users db/conn-spec)
          affected        (count (keep identity response))
          item-params     (set (map (comp set keys) response))]
      (is (= (count emails) (count response)))
      (is (= (count users-to-delete) affected))
      (is (= #{:id :email :created_at :deleted_at} (first item-params)))
      (is (= (count users-before) (count users-after)))))

  (testing "delete nonexistent users"
    (let [ids          (repeatedly 2 #(UUID/randomUUID))
          users-before (sut/get-all-app-users db/conn-spec)
          response     (sut/batch-mark-deleted-app-user db/conn-spec {:ids (vec ids)})
          users-after  (sut/get-all-app-users db/conn-spec)
          affected     (count (keep identity response))]
      (is (= (count users-before) (count users-after)))
      (is (zero? affected)))))

(deftest ^:integration get-all-not-deleted-users-test
  (let [emails       ["get_not_deleted_test1@example.com" "get_not_deleted_test2@example.com" "get_not_deleted_test3@example.com"]
        inserted     (sut/batch-insert-app-user db/conn-spec {:users (mapv vector emails)})
        users-before (sut/get-all-app-users db/conn-spec)
        soft-deleted (sut/batch-mark-deleted-app-user db/conn-spec {:ids (mapv :id inserted)})
        users-after  (sut/get-all-app-users db/conn-spec)
        not-deleted  (sut/get-all-not-deleted-users db/conn-spec)]
    (is (= (count users-before) (count users-after)))
    (is (= (count inserted) (count soft-deleted)))
    (is (= (count emails) (count inserted)))
    (is (= (- (count users-after) (count soft-deleted)) (count not-deleted)))))