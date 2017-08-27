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
              (h/not h/first-child))
            body))

(defn line-break? [element]
  (and
    (string? element)
    (s/blank? (s/trim element))))

(defn remove-linebreaks-from-lists
  [element]
  (if (or (vector? element) (seq? element) (map? element))
    (remove line-break? element)
    element))

(defn get-all-events-without-line-breaks
  [file]
  (w/prewalk
    remove-linebreaks-from-lists
    (get-upcoming-events (parse-all-events file))))


(defn -main
  [& args]
  (println (get-all-events-without-line-breaks path)))
