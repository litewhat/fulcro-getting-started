(ns app.ui
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]
            [app.person.mutations :as mut]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]))

(defsc ClickCounter
  [this {:keys [:counter/clicks :counter/name]}]
  {}
  (dom/div {:id (str "ClickCounter-" name)}
    (dom/h1 "Counter")
    (dom/p "My name is " name)
    (dom/p (str "Clicked: " clicks " times"))))

(defsc Person
  [this {:keys [:person/id :person/name :person/age] :as props} {:keys [onDelete]}]
  {:query         [:person/id :person/name :person/age]
   :ident         (fn [] [:person/id (:person/id props)])}
  (dom/li
    (dom/h5 (str name " " "(age: " age ")"))
    (dom/button {:onClick #(onDelete id)} "X")))

(def ui-person (comp/computed-factory Person {:keyfn :person/id}))

(defsc PersonList
  [this {:keys [:list/id :list/label :list/people] :as props}]
  {:query         [:list/id :list/label {:list/people (comp/get-query Person)}]
   :ident         (fn [] [:list/id (:list/id props)])}
  (let [delete-person (fn [person-id]
                        (comp/transact! this [(mut/delete-person {:list/id id :person/id person-id})]))]
    (dom/div
      (dom/h4 label)
      (dom/ul
        (map (fn [p] (ui-person p {:onDelete delete-person})) people)))))

(def ui-person-list (comp/factory PersonList))

(defsc Root
  [this {:keys [friends enemies]}]
  {:query         [{:friends (comp/get-query PersonList)}
                   {:enemies (comp/get-query PersonList)}]
   :initial-state {}}
  (dom/div :.container
    (dom/h1 "Root component")
    (dom/div
      (when friends
        (ui-person-list friends))
      (when enemies
        (ui-person-list enemies)))))



(comment
  (comp/computed-factory Person {:keyfn :person/name}))

(comment
  (comp/get-initial-state Person {:name "Paweł" :age 28})
  (comp/get-query Person)

  (meta (comp/get-query PersonList))
  (meta (comp/get-query Person)))

(comment
  ;; require
  ;; [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]
  (fdn/db->tree [{:friends [:list/label]}] (comp/get-initial-state Root {}) {})
  (fdn/db->tree [{:enemies [:list/label {:list/people [:person/name]}]}] (comp/get-initial-state Root {}) {}))

(comment
  (df/load! this [:person/id 3] Person {:target (targeting/append-to [:list/id :friends :list/people])}))