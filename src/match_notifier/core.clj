(ns match-notifier.core
  (:gen-class))

(use 'hickory.core)
(use 'hickory.zip)
(require '[hickory.select :as h])

(def path "test/resources/overview.html")

(defn hickory-snippets
  [file]
  (as-hickory (parse (slurp file))))

(defn one-element?
  [elements]
  (= 1 (count elements)))

(defn running-tournament-element
  [events]
  (h/select
    (h/find-in-text #"laufende Turniere")
    events))

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
  [snippets]
  (->> snippets
       running-tournament-element
       one-element?))

(defn tournaments
  [snippets]
  (->> snippets
       running-tournament-snippets
       (map to-tournament)))

(defn running-tournaments
  [snippets]
  (if (running-tournaments? snippets)
    (tournaments snippets)
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
                (running-tournaments (hickory-snippets path)))))
