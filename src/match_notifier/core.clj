(ns match-notifier.core
  (:gen-class))

(use 'hickory.core)
(use 'hickory.zip)
(require '[hickory.select :as h]
         '[clojure.walk :as w]
         '[clojure.string :as s])

(def path "test/resources/overview.html")

(defn parse-all-events
  [file]
  (as-hickory (parse (slurp file))))

(defn get-upcoming-events
  [body]
  (h/select (h/child
              (h/id :tabelle)
              (h/nth-child 2)
              (h/tag :tbody)
              (h/not h/first-child)
              (h/nth-child 2)
              (h/tag :a))
            body))

(defn get-running-events
  [body]
  (h/select (h/child
              (h/id :tabelle)
              (h/nth-child 1)
              (h/tag :tbody)
              (h/not h/first-child)
              (h/nth-child 2)
              (h/tag :a))
            body))

(defn get-upcoming-events-from-file
  [file]
  (get-upcoming-events (parse-all-events file)))

(defn get-running-events-from-file
  [file]
  (get-running-events (parse-all-events file)))

(defn project-tournament-data
  [snippet]
  {:link (first (:content snippet))
   :name (get-in snippet [:attrs :href])})

(defn -main
  [& args]
  (println (map project-tournament-data
                (get-running-events-from-file path))))

(defn line-break? [element]
  (and
    (string? element)
    (s/blank? (s/trim element))))

(defn remove-linebreaks-from-lists
  [element]
  (println element)
  (if (vector? element)
    (remove line-break? element)
    element))

(defn get-upcoming-events-without-line-breaks
  [file]
  (w/prewalk
    remove-linebreaks-from-lists
    (get-upcoming-events (parse-all-events file))))

(defn get-running-events-without-line-breaks
  [file]
  (w/prewalk
    remove-linebreaks-from-lists
    (parse-all-events file)))

(def running-tournament-selector
  (h/child
    (h/tag :table)
    (h/tag :thead)
    (h/tag :tr)
    (h/tag :th)))

(def create-filter-table-name-function
  (comp #(= "laufende Turniere" %) first :content first))

(defn running-tournaments?
  [path]
  (create-filter-table-name-function
    (h/select
      running-tournament-selector
      (parse-all-events path))))
