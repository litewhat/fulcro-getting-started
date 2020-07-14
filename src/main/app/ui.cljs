(ns app.ui
  (:require [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.data-fetch :as df]
            [com.fulcrologic.fulcro.dom :as dom]
            [taoensso.timbre :as log]
            [app.person.mutations :as mut]
            [app.user-registration.ui :as ur.ui]))

(defsc ClickCounter
  [this {:keys [:counter/id :counter/clicks] :as props}]
  {:query         [:counter/id :counter/clicks]
   :ident         (fn [] [:counter/id (:counter/id props)])
   :initial-state {:counter/id 0 :counter/clicks 0}}
  (dom/div {:id (str "ClickCounter-" id)}
    (dom/p "Identifier: " id)
    (dom/p "Clicked: " (dom/b clicks) " times")
    (dom/button {:onClick #(comp/transact! this [(mut/increase-counter {:counter/id id})])}
      "Click!")))

(def ui-click-counter (comp/factory ClickCounter))

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
  [this {:keys [friends enemies click-counter user-registration]}]
  {:query         [{:friends (comp/get-query PersonList)}
                   {:enemies (comp/get-query PersonList)}
                   {:click-counter (comp/get-query ClickCounter)}
                   {:user-registration (comp/get-query ur.ui/UserRegistration)}]
   :initial-state (fn [params]
                    {:user-registration (comp/get-initial-state ur.ui/UserRegistration)
                     :click-counter     (comp/get-initial-state ClickCounter)})}
  (dom/div :.container
    (dom/div :.container
      (dom/h1 "Registration")
      (ur.ui/ui-user-registration user-registration))
    (dom/div :.container
      (dom/h1 "Counter")
      (dom/div
        (ui-click-counter click-counter)))
    (dom/div :.container
      (dom/h1 "People")
      (dom/div
        (when friends
          (ui-person-list friends))
        (when enemies
          (ui-person-list enemies))))))

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
  ;; [com.fulcrologic.fulcro.algorithms.normalize :as fnm]
  (fdn/db->tree [{:friends [:list/label]}] (comp/get-initial-state Root {}) {})
  (fdn/db->tree [{:enemies [:list/label {:list/people [:person/name]}]}] (comp/get-initial-state Root {}) {})
  (fdn/db->tree [{:click-counter [:counter/id :counter/clicks]}] (comp/get-initial-state Root {}) {})

  (com.fulcrologic.fulcro.application/current-state app.application/app)

  (fnm/tree->db ClickCounter {:click-counter {:counter/id 1 :counter/clicks 6}})
  )

(comment
  (df/load! this [:person/id 3] Person {:target (targeting/append-to [:list/id :friends :list/people])}))