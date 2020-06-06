(ns dotenv
   (:require [clojure.string :as str]
             [clojure.java.io :as io]
             [clojure.core.strint :refer [<<]]))

(defn- unquote-doublequoted-string [string]
  (-> string
      (str/replace #"^\"|\"$" "")
      (str/replace #"\\\"" "\"")))

(defn- unquote-singlequoted-string [string]
  (-> string
      (str/replace #"^'|'$" "")
      (str/replace #"\\'" "'")))

(defn- unquote-string [string]
  (cond (str/starts-with? string "\"") (unquote-doublequoted-string string)
        (str/starts-with? string "'")  (unquote-singlequoted-string string)
        :else string))

(defn- to-pairs [rawstring]
  "converts a string containing the contents of a .env file into a list of pairs"
  (->> rawstring
       (str/split-lines)                             ; split input by linebreak
       (map str/trim)                                ; trim heading or tailing spaces
       (remove #(-> % empty?                         ; discard empty lines
                      (str/starts-with? "#")))       ; discard commented lines
       (map #(str/split % #"="))                     ; split by equal
       (map #(let [[h & t] %]
               [(str/replace h #"export *" "")       ; handle "exports declarations"
                (str/join "=" t) ]))                 ; join back values that got split
       (map #(vec (->> % (map str/trim)              ; trim whitespaces on var and value
                         (map unquote-string))))     ; unquote values
       ))

(defn- load-env-file [filename]
  "loads an env file into a map"
  (if (.exists (io/as-file filename))
    (->> filename
         slurp
         to-pairs
         (into {}) )
    {}))

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

(def app-env-specific-filenames [(<< ".env.~{app-env}")])

(def app-env-specific-env
  (into {} (map load-env-file app-env-specific-filenames)))

(def extended-env
  (into {} [base-env app-env-specific-env]))

(defn env
  ([] extended-env)
  ([k] (extended-env (name k))))
