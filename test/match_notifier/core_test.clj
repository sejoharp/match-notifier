(ns match-notifier.core-test
  (:require [clojure.test :refer :all]
            [match-notifier.core :refer :all]))

(def overview-path "test/resources/overview.html")
(def overview-without-running-tournaments-path "test/resources/overview-without-running-tournaments.html")

(def tournament-path "test/resources/tournament.html")

(deftest overview-parsing
  (testing "detects running tournaments"
    (is (= (running-tournaments? overview-path) true))
    (is (= (running-tournaments? overview-without-running-tournaments-path) false))))
