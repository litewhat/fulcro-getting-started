(ns app.person.db.queries-test
  (:require [clojure.test :refer [deftest testing is are use-fixtures]]
            [taoensso.timbre :as log]
            [app.db :as db]
            [app.person.db.queries :as sut]))

(defn db-fixture [f]
  (log/debug "Setting up tables")
  (db/set-up-tables! db/conn-spec)
  (f)
  (log/debug "Tearing down tables")
  (db/tear-down-tables! db/conn-spec))

(use-fixtures :each db-fixture)

(deftest ^:integration queries-test
  (testing "empty person table after initalization"
    (is (= 0 (count (sut/get-all-people db/conn-spec)))))

  (testing "insert person"
    (sut/insert-person db/conn-spec {:name "Paweł" :age 28})
    (sut/insert-person db/conn-spec {:name "Josh" :age 34})
    (sut/insert-person db/conn-spec {:name "Andrea" :age 25})
    (sut/insert-person db/conn-spec {:name "Eduardo" :age 26})
    (sut/insert-person db/conn-spec {:name "Richard" :age 60})
    (sut/insert-person db/conn-spec {:name "Michael" :age 64})
    (sut/insert-person db/conn-spec {:name "Sarah" :age 18})
    (sut/insert-person db/conn-spec {:name "Jacob" :age 16})
    (sut/insert-person db/conn-spec {:name "Marc" :age 24})

    (is (= 9 (count (sut/get-all-people db/conn-spec)))))

  (testing "resolving person by id"
    (are [id person] (let [{:keys [name age]} person]
                       (= {:id id :name name :age age}
                          (select-keys (sut/get-person-by-id db/conn-spec {:id id})
                                       [:id :name :age])))
      1 {:id 1 :name "Paweł" :age 28}
      2 {:id 2 :name "Josh" :age 34}
      3 {:id 3 :name "Andrea" :age 25}
      4 {:id 4 :name "Eduardo" :age 26}
      5 {:id 5 :name "Richard" :age 60}
      6 {:id 6 :name "Michael" :age 64}
      7 {:id 7 :name "Sarah" :age 18}
      8 {:id 8 :name "Jacob" :age 16}
      9 {:id 9 :name "Marc" :age 24})

    (are [id] (nil? (sut/get-person-by-id db/conn-spec {:id id}))
      10
      11
      12))

  (testing "insert person list"
    (sut/insert-person-list db/conn-spec {:id (str :friends)})
    (sut/insert-person-list db/conn-spec {:id (str :enemies)})

    (let [res    (sut/get-all-person-lists db/conn-spec)
          plists (map #(update % :id read-string) res)]
      (is (= 2 (count plists)))
      (is (= #{:friends :enemies} (set (map :id plists)))))

    (is (thrown-with-msg?
          java.sql.BatchUpdateException
          #"ERROR: duplicate key value violates unique constraint \"person_list_pkey\""
          (sut/insert-person-list db/conn-spec {:id (str :enemies)}))))

  (testing "resolving person list by id"
    (let [friends (sut/get-person-list-by-id db/conn-spec {:id (str :friends)})
          enemies (sut/get-person-list-by-id db/conn-spec {:id (str :enemies)})]
      (is (.before (:created_at friends) (:created_at enemies))))

    (is (nil? (sut/get-person-list-by-id db/conn-spec {:id (str :someName)}))))

  (testing "adding person to list individually"
    (sut/add-person-to-list db/conn-spec {:list_id   (str :friends)
                                          :person_id 2})
    (sut/add-person-to-list db/conn-spec {:list_id   (str :friends)
                                          :person_id 4})
    (sut/add-person-to-list db/conn-spec {:list_id   (str :friends)
                                          :person_id 6})
    (sut/add-person-to-list db/conn-spec {:list_id   (str :enemies)
                                          :person_id 1})
    (sut/add-person-to-list db/conn-spec {:list_id   (str :enemies)
                                          :person_id 3})
    (sut/add-person-to-list db/conn-spec {:list_id   (str :enemies)
                                          :person_id 5})
    (sut/add-person-to-list db/conn-spec {:list_id   (str :enemies)
                                          :person_id 7})

    (let [fres    (sut/get-people-by-list-id db/conn-spec {:list_id (str :friends)})
          eres    (sut/get-people-by-list-id db/conn-spec {:list_id (str :enemies)})
          friends (map #(update % :list_id read-string) fres)
          enemies (map #(update % :list_id read-string) eres)]
      (is (= 3 (count friends)))
      (is (= 4 (count enemies)))
      (is (= #{2 4 6} (set (map :person_id friends))))
      (is (= #{1 3 5 7} (set (map :person_id enemies)))))

    (is (thrown-with-msg?
          java.sql.BatchUpdateException
          #"ERROR: duplicate key value violates unique constraint \"person_list_people_pkey\""
          (sut/add-person-to-list db/conn-spec {:list_id   (str :friends)
                                                :person_id 2}))))

  (testing "adding person to list at one go"
    (let [list-id    (str :friends)
          people-ids [8 9]
          affected   (sut/add-people-to-list db/conn-spec {:people (mapv (partial vector list-id) people-ids)})
          fres       (sut/get-people-by-list-id db/conn-spec {:list_id (str :friends)})
          friends    (map #(update % :list_id read-string) fres)]
      (is (= 2 affected))
      (is (= 5 (count friends))))

    (let [list-id    (str :friends)
          people-ids [8 9]]
     (is (thrown-with-msg?
           java.sql.BatchUpdateException
           #"ERROR: duplicate key value violates unique constraint \"person_list_people_pkey\""
           (sut/add-people-to-list db/conn-spec {:people (mapv (partial vector list-id) people-ids)}))))))