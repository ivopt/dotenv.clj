(ns dotenv
   (:require [clojure.string :as str]
             [clojure.java.io :as io]
             [clojure.core.strint :refer [<<]]))

(def app-env-vars ["APP_ENV" "LEIN_ENV" "BOOT_ENV"])

(def app-env
  (or
    (->> app-env-vars
         (map #(System/getenv %))
         (filter some?)
         (first))
    "development"))

(def local-env-files
  (filter #(.exists (io/file %)) [".env" (<< ".env.~{app-env}")]))

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

; Loads local env...
(def local-env
  (->> local-env-files
       (map load-env-file)
       (into {}) ))

(def env-sources [local-env (System/getenv) (System/getProperties)])

(defn- load-env [] (into {} env-sources))

(def env (load-env))
(defn env-key [kw] (env (name kw)))
