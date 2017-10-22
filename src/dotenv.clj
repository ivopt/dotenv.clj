(ns dotenv
   (:require [clojure.string :as str]
             [clojure.java.io :as io]
             [clojure.core.strint :refer [<<]]))

(defn- to-pairs [rawstring]
  "converts a string containing the contents of a .env file into a list of pairs"
  (let [lines (str/split-lines rawstring)]
    (map #(str/split % #"=" 2) lines)))

(defn- load-env-file [filename]
  "loads an env file into a map"
  (->> filename
       slurp
       to-pairs
       (into {}) ))

(def base-env
  (into {} [
            (System/getenv)
            (System/getProperties)
            (load-env-file ".env")
           ]))

(def app-env-vars ["APP_ENV" "LEIN_ENV" "BOOT_ENV"])
(def app-env
  (or
    (->> app-env-vars
         (map #(base-env %))
         (filter some?)
         (first))
    "development"))

(def app-env-specific-filenames
  (filter #(.exists (io/file %)) [(<< ".env.~{app-env}")]))

(def app-env-specific-env
  (into {} (map load-env-file app-env-specific-filenames)))

(def extended-env
  (into {} [base-env app-env-specific-env]))

(defn env
  ([] extended-env)
  ([k] (extended-env (name k))))
