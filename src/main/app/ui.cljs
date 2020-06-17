(ns app.ui
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]))

(defsc ClickCounter
  [this {:keys [:counter/clicks :counter/name]}]
  {}
  (js/console.log this)
  (dom/div {:id (str "ClickCounter-" name)}
    (dom/h1 "Counter")
    (dom/p "My name is " name)
    (dom/p (str "Clicked: " clicks " times"))))

(defsc Person
  [this {:keys [:person/name :person/age]} {:keys [onDelete]}]
  {:query         [:person/name :person/age]
   :initial-state (fn [{:keys [name age] :as params}] {:person/name name :person/age age})}
  (js/console.log "Computed values:" (comp/get-computed this))
  (dom/li
    (dom/h5 (str name " " "(age: " age ")"))
    (dom/button {:onClick #(onDelete name)} "X")))

(def ui-person (comp/factory Person {:keyfn :person/name}))


(defsc PersonList
  [this {:keys [:list/label :list/people]}]
  {:query [:list/label {:list/people (comp/get-query Person)}]
   :initial-state (fn [{:keys [label]}]
                    {:list/label  label
                     :list/people (case label
                                    "Friends" [(comp/get-initial-state Person {:name "Andrew" :age 26})
                                               (comp/get-initial-state Person {:name "Diana" :age 18})]
                                    "Enemies" [(comp/get-initial-state Person {:name "Jonathan" :age 45})
                                               (comp/get-initial-state Person {:name "Daren" :age 25})]
                                    [])})}
  (let [delete-person (fn [pname] (js/console.log "Asked to delete" pname))]
   (dom/div
     (dom/h4 label)
     (dom/ul
       (map (fn [p] (ui-person (comp/computed p {:onDelete delete-person}))) people)))))

(def ui-person-list (comp/factory PersonList))

(defsc Root
  [this {:keys [friends enemies]}]
  {:query         [{:friends (comp/get-query PersonList)}
                   {:enemies (comp/get-query PersonList)}]
   :initial-state (fn [params]
                    {:friends (comp/get-initial-state PersonList {:label "Friends"})
                     :enemies (comp/get-initial-state PersonList {:label "Enemies"})})}
  (js/console.log "Root" this)
  (js/console.log "Props of Root:" (comp/props this))
  (dom/div :.container
    (dom/h1 "Root component")
    (dom/div
      (ui-person-list friends)
      (ui-person-list enemies))))


(comment
  (comp/get-initial-state Person {:name "Paweł" :age 28}))

(comment
  (comp/get-query Person))

(comment
  (comp/computed-factory Person {:keyfn :person/name}))

(comment
  (require '[com.fulcrologic.fulcro.algorithms.denormalize :as fdn])
  (fdn/db->tree [{:friends [:list/label]}] (comp/get-initial-state Root {}) {})
  (fdn/db->tree [{:enemies [:list/label {:list/people [:person/name]}]}] (comp/get-initial-state Root {}) {}))