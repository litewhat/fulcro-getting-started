(ns app.person.db.queries-test
  (:require [clojure.test :refer [deftest testing is are use-fixtures]]
            [db.seed :as dbs]
            [app.db :as db]
            [app.test.fixtures :as fixtures]
            [app.person.db.queries :as sut]))

(use-fixtures :each (fixtures/db db/conn-spec))

(deftest ^:integration insert-person-test
  (sut/insert-person db/conn-spec {:name "Paweł" :age 28})
  (sut/insert-person db/conn-spec {:name "Josh" :age 34})
  (sut/insert-person db/conn-spec {:name "Andrea" :age 25})
  (sut/insert-person db/conn-spec {:name "Eduardo" :age 26})
  (sut/insert-person db/conn-spec {:name "Richard" :age 60})
  (sut/insert-person db/conn-spec {:name "Michael" :age 64})
  (sut/insert-person db/conn-spec {:name "Sarah" :age 18})
  (sut/insert-person db/conn-spec {:name "Jacob" :age 16})
  (sut/insert-person db/conn-spec {:name "Marc" :age 24})

  (is (= (+ 9 (count dbs/people)) (count (sut/get-all-people db/conn-spec)))))

(deftest ^:integration get-person-by-id-test
  (testing "resolving person by id"
    (are [id person] (let [[name age] person
                           person-record (juxt :name :age)]
                       (= [name age] (person-record (sut/get-person-by-id db/conn-spec {:id id}))))
      1 ["Paweł" 28]
      2 ["Andrea" 25]
      3 ["Josh" 34]
      4 ["Richard" 60]
      5 ["Eduardo" 26]
      6 ["Michael" 64]
      7 ["Sarah" 18]
      8 ["Jacob" 16]
      9 ["Marc" 24]
      10 ["Serge" 78]
      11 ["Luc" 42]
      12 ["Andy" 14])

    (are [id] (nil? (sut/get-person-by-id db/conn-spec {:id id}))
      13
      14
      15)))

(deftest ^:integration insert-person-list-test
  (testing "insert person list"
    (sut/insert-person-list db/conn-spec {:id (str :unknown)})
    (let [person-lists (map #(update % :id read-string) (sut/get-all-person-lists db/conn-spec))]
      (is (= 3 (count person-lists)))
      (is (= #{:friends :enemies :unknown} (set (map :id person-lists)))))

    (is (thrown-with-msg?
          java.sql.BatchUpdateException
          #"ERROR: duplicate key value violates unique constraint \"person_list_pkey\""
          (sut/insert-person-list db/conn-spec {:id (str :enemies)})))))

(deftest ^:integration get-person-list-by-id-test
  (testing "resolving person list by id"
    (let [friends (sut/get-person-list-by-id db/conn-spec {:id (str :friends)})
          enemies (sut/get-person-list-by-id db/conn-spec {:id (str :enemies)})]
      (is (.equals (:created_at friends) (:created_at enemies))))

    (is (nil? (sut/get-person-list-by-id db/conn-spec {:id (str :someName)})))))

(deftest ^:integration add-person-to-list-test
  (testing "adding person to list individually"
    (let [friends-before (map #(update % :list_id read-string) (sut/get-people-by-list-id db/conn-spec {:list_id (str :friends)}))
          enemies-before (map #(update % :list_id read-string) (sut/get-people-by-list-id db/conn-spec {:list_id (str :enemies)}))
          _              (sut/add-person-to-list db/conn-spec {:list_id (str :friends) :person_id 7})
          _              (sut/add-person-to-list db/conn-spec {:list_id (str :enemies) :person_id 8})
          friends-after  (map #(update % :list_id read-string) (sut/get-people-by-list-id db/conn-spec {:list_id (str :friends)}))
          enemies-after  (map #(update % :list_id read-string) (sut/get-people-by-list-id db/conn-spec {:list_id (str :enemies)}))]
      (is (= #{1 3 5} (set (map :person_id friends-before))))
      (is (= #{2 4 6} (set (map :person_id enemies-before))))
      (is (= #{1 3 5 7} (set (map :person_id friends-after))))
      (is (= #{2 4 6 8} (set (map :person_id enemies-after)))))

    (is (thrown-with-msg?
          java.sql.BatchUpdateException
          #"ERROR: duplicate key value violates unique constraint \"person_list_people_pkey\""
          (sut/add-person-to-list db/conn-spec {:list_id (str :enemies) :person_id 2})))))

(deftest ^:integration add-people-to-list-test
  (testing "adding person to list at one go"
    (sut/insert-person-list db/conn-spec {:id (str :unknown)})
    (let [list-id      (str :unknown)
          people-ids   [9 10]
          affected     (sut/add-people-to-list db/conn-spec {:people (mapv (partial vector list-id) people-ids)})
          list-members (map #(update % :list_id read-string) (sut/get-people-by-list-id db/conn-spec {:list_id (str :unknown)}))]
      (is (= 2 affected))
      (is (= 2 (count list-members)))
      (is (= #{9 10} (set (map :person_id list-members)))))

    (let [list-id    (str :unknown)
          people-ids [9 10]]
      (is (thrown-with-msg?
            java.sql.BatchUpdateException
            #"ERROR: duplicate key value violates unique constraint \"person_list_people_pkey\""
            (sut/add-people-to-list db/conn-spec {:people (mapv (partial vector list-id) people-ids)}))))))

(deftest ^:integration remove-person-from-list-test
  (testing "removing person form list when both exists"
    (let [friends-affected (sut/remove-person-from-list db/conn-spec {:list_id (str :friends) :person_id 5})
          enemies-affected (sut/remove-person-from-list db/conn-spec {:list_id (str :enemies) :person_id 6})
          friends          (map #(update % :list_id read-string) (sut/get-people-by-list-id db/conn-spec {:list_id (str :friends)}))
          enemies          (map #(update % :list_id read-string) (sut/get-people-by-list-id db/conn-spec {:list_id (str :enemies)}))]
      (is (= 1 friends-affected))
      (is (= 1 enemies-affected))
      (is (= #{1 3} (set (map :person_id friends))))
      (is (= #{2 4} (set (map :person_id enemies))))))

  (testing "removing person form list when list does not exist"
    (let [list-id  (str :non-existent-list)
          affected (sut/remove-person-from-list db/conn-spec {:list_id list-id :person_id 6})
          res      (sut/get-people-by-list-id db/conn-spec {:list_id list-id})]
      (is (zero? affected))
      (is (empty? res))))

  (testing "removing person form list when person does not exist"
    (let [friends-affected (sut/remove-person-from-list db/conn-spec {:list_id (str :friends) :person_id 99999})
          friends          (map #(update % :list_id read-string) (sut/get-people-by-list-id db/conn-spec {:list_id (str :friends)}))]
      (is (zero? friends-affected))
      (is (= #{1 3} (set (map :person_id friends)))))))

(deftest ^:integration batch-insert-person-test
  (let [people-tuples [["TestBatch1" 15] ["TestBatch2" 35]]
        people-before (sut/get-all-people db/conn-spec)
        inserted      (sut/batch-insert-person db/conn-spec {:people people-tuples})
        people-after  (sut/get-all-people db/conn-spec)]
    (is (= (count people-tuples) inserted))
    (is (= (+ inserted (count people-before)) (count people-after)))))

(deftest ^:integration batch-insert-person-list-test
  (testing "inserting many lists at one go"
    (let [list-tuples  (map (comp vector str) #{:kind :unpleasant})
          lists-before (map #(update % :id read-string) (sut/get-all-person-lists db/conn-spec))
          inserted     (sut/batch-insert-person-list db/conn-spec {:person_lists list-tuples})
          lists-after  (map #(update % :id read-string) (sut/get-all-person-lists db/conn-spec))]

      (is (= 2 inserted))
      (is (= #{:friends :enemies} (set (map :id lists-before))))
      (is (= #{:friends :enemies :kind :unpleasant} (set (map :id lists-after))))

      (testing "inserting many lists when some of them already exist"
        (let [tuples       (map (comp vector str) #{:kind :unpleasant :tall :small})
              lists-before (map #(update % :id read-string) (sut/get-all-person-lists db/conn-spec))]
          (is (thrown-with-msg?
                java.sql.BatchUpdateException
                #"ERROR: duplicate key value violates unique constraint \"person_list_pkey\""
                (sut/batch-insert-person-list db/conn-spec {:person_lists tuples})))
          (let [lists-after (map #(update % :id read-string) (sut/get-all-person-lists db/conn-spec))]
            (is (= lists-before lists-after))))))))
