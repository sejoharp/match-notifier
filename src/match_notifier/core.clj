(ns match-notifier.core
  (:require [hickory.select :as h]
           [falcon.core :as falcon]
           [clojure.java.io :as io]))

(def path "resources/overview.html")

(defn parsed-html
  [file]
  (falcon/parse (io/resource file)))

(defn one-element?
  [elements]
  (= 1 (count elements)))

(defn running-tournament-element
  [events]
    (falcon/select events "table.table:nth-child(2) > thead:nth-child(1) > tr:nth-child(1) > th:nth-child(1):contains(laufende Turniere)"))

(defn running-matches-element
  [events]
  (h/select
    (h/find-in-text #"laufende Spiele")
    events))

(defn running-tournament-snippets
  [html]
  (h/select
    (h/child
      (h/id :c1)
      (h/nth-child 2)
      (h/tag :tbody)
      (h/tag :tr)
      (h/tag :td)
      (h/tag :span)
      (h/tag :a))
    html))


(def name (comp first :content))
(def link (comp #(str "http://www.tifu.info/" %) :href :attrs))

(defn to-tournament
  [snippet]
  {:name (name snippet)
   :link (link snippet)})

(defn running-tournaments?
  [html]
  (->> html
       running-tournament-element
       one-element?))

(defn tournaments
  [html]
  (->> html
       running-tournament-snippets
       (map to-tournament)))

(defn running-tournaments
  [html]
  (if (running-tournaments? html)
    (tournaments html)
    []))

(defn running-matches?
  [snippets]
  (->> snippets
       running-matches-element
       one-element?))

(defn running-matches
  [snippets]
  (h/select
    (h/child
      (h/id :c4)
      (h/nth-child 2)
      (h/tag :tbody)
      (h/tag :tr))
    snippets))

(defn nth-element-from-event
  [position snippet]
  (h/select (h/child (h/nth-child position)) snippet))

(defn get-element-from-snippet
  [position snippet]
  (->> snippet
       (nth-element-from-event position)
       (first)
       (:content)
       (first)))

(defn tableNumber
  [snippet]
  (get-element-from-snippet 1 snippet))

(defn discipline
  [snippet]
  (get-element-from-snippet 2 snippet))

(defn round
  [snippet]
  (get-element-from-snippet 3 snippet))


(defn -main
  [& args]
  (println (map to-tournament
                (running-tournaments (parsed-html path)))))
