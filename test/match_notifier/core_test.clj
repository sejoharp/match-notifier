(ns match-notifier.core-test
  (:require [clojure.test :refer :all]
            [match-notifier.core :refer :all]))

(def overview-snippets (hickory-snippets "test/resources/overview.html"))
(def overview-snippets-without-running-tournaments (hickory-snippets "test/resources/overview-without-running-tournaments.html"))

(def tournament-snippets (hickory-snippets "test/resources/tournament.html"))
(def tournament-snippets-without-running-match (hickory-snippets "test/resources/tournament-without-running-match.html"))

(deftest overview-parsing
  (testing "detects running tournaments"
    (is (=
          (running-tournaments? overview-snippets)
          true)))
  (testing "detects no running tournaments"
    (is (=
          (running-tournaments? overview-snippets-without-running-tournaments)
          false)))
  (testing "finds a running tournament"
    (is (=
          (first (running-tournaments overview-snippets))
          {:name "Einzelturnierserie Kixx"
            :link "http://www.tifu.info/turnier?turnierid=177&ver=2"})))
  (testing "finds no running tournament"
    (is (=
          (running-tournaments overview-snippets-without-running-tournaments)
          []))))

(deftest tournament-parsing
  (testing "finds a running match"
    (is (=
          (running-matches? tournament-snippets)
          true)))
  (testing "finds no running match"
    (is (=
          (running-matches? tournament-snippets-without-running-match)
          false)))
  (testing "returns a running match"
    (is (=
          (first (running-matches tournament-snippets))
          [{:table 1
            :discipline "MD Profi"
            :round "1/8"
            :team1 "Mia Reimer / Arne Borck"
            :team2 "Michael Strauß / Petra Andres"}]))))

