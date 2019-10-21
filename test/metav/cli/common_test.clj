(ns metav.cli.common-test
  (:require
    [clojure.test :as test :refer [deftest testing]]
    [clojure.edn :as edn]
    [testit.core :refer :all]
    [metav.cli.common :as m-c-c]))

;; TODO

(def common-validate
  (m-c-c/make-validate-args m-c-c/cli-options
                            (fn [text]
                              (str "usage summary \n" text))))

(deftest common-validation-parsing
  (testing "Bad options raises an error:"
    (testing "Unknown option"
      (let [res (common-validate ["-b"])]
        (fact
          res =in=> {:exit? true
                     :exit-message truthy
                     :ok? false})))

    (testing "Version scheme for instance is validated"
      (let [res (common-validate ["-s" "scheme"])]
        (fact
          res =in=> {:exit? true
                     :exit-message truthy
                     :ok? false}))

      (let [res (common-validate ["-s" ":maven"])]
        (fact
          res =in=> {:exit? false
                     :ok? true})))

    (testing "We can load option from a config file."
      (let [res (common-validate ["-c" "resources-test/example-conf.edn"])
            conf (-> "resources-test/example-conf.edn" slurp edn/read-string)]
        (fact
          (:ctxt-opts res) =in=> conf)))

    (testing "Command line option override config from file."
      (let [res (common-validate ["-c" "resources-test/example-conf.edn"])
            overridden-res (common-validate ["-c" "resources-test/example-conf.edn"
                                             "-s" "maven"])]
        (facts
          (:ctxt-opts res)            =in=> {:metav/version-scheme :semver}
          (:ctxt-opts overridden-res) =in=> {:metav/version-scheme :maven})))))


(test/run-tests)
