(ns match-notifier.core
  (:gen-class))

(use 'hickory.core)
(use 'hickory.zip)
(require '[hickory.select :as h]
         '[clojure.walk :as w]
         '[clojure.string :as s])

(def path "test/resources/overview.html")

(defn hickory-snippets
  [file]
  (as-hickory (parse (slurp file))))

(defn project-tournament
  [snippet]
  {:link (first (:content snippet))
   :name (get-in snippet [:attrs :href])})

(defn element-count
  [elements]
  (= 1 (count elements)))

(defn running-tournament-element
  [events]
  (h/select
    (h/find-in-text #"laufende Turniere")
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

(def link (comp #(str "http://www.tifu.info/" %) :href :attrs))

(def name (comp first :content))

(defn to-tournament
  [snippet]
  {:name (name snippet)
   :link (link snippet)})

(defn running-tournaments?
  [path]
  (->> path
       hickory-snippets
       running-tournament-element
       element-count))

(defn tournaments
  [path]
  (->> path
       hickory-snippets
       running-tournament-snippets
       (map to-tournament)))


(defn running-tournaments
  [path]
  (if (running-tournaments? path)
    (tournaments path)
    []))

(defn -main
  [& args]
  (println (map project-tournament
                (running-tournaments path))))
