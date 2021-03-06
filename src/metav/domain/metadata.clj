(ns metav.domain.metadata
  (:require
   [clojure.string :as string])
  (:import [java.util Date TimeZone]
           [java.text SimpleDateFormat]))

;; TODO: rename this ns to metadata
(defn iso-now []
  (let [tz (TimeZone/getTimeZone "UTC")
        df (SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ss'Z'")]
    (.setTimeZone df tz)
    (.format df (Date.))))


(defn metadata-as-edn [context]
  (let [{:metav/keys [artefact-name version tag git-prefix]} context]
    {:module-name artefact-name
     :version (str version)
     :tag tag
     :generated-at (iso-now)
     :path (if git-prefix git-prefix ".")}))


(defn metadata-as-code
  [context]
  (let [{:metav.spit/keys [namespace]} context
        {:keys [module-name path version tag generated-at]} (metadata-as-edn context)]
    (string/join "\n" [";; This code was automatically generated by the 'metav' library."
                       (str "(ns " namespace ")") ""
                       (format "(def module-name \"%s\")" module-name)
                       (format "(def path \"%s\")" path)
                       (format "(def version \"%s\")" version)
                       (format "(def tag \"%s\")" tag)
                       (format "(def generated-at \"%s\")" generated-at)
                       ""])))
