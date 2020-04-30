;; practicalli.data-gov-uk
;;
;; Data taken from Gov.uk website
;; https://www.gov.uk/government/publications/covid-19-track-coronavirus-cases
;; Data only available as Microsoft Excel documents
;; There is no JSON format API
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns practicalli.data-gov-uk
  (:require [clojure.java.io   :as io]
            [clojure.data.csv  :as csv]
            [semantic-csv.core :as semantic-csv]))



;; Extract data from CVS files
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; New combined data from Gov.uk
;; April 14th 2020 onwards


(defn extract-data-from-csv
  "Convert CSV file to sequence of vectors
  Each hash-map uses the heading text as a key
  for each element in the row of data.

  Return: a sequence of vectors"
  [data-source]
  (->> data-source
       io/resource
       slurp
       csv/read-csv))

(def covid19-uk-england-combined-data
  (extract-data-from-csv "data-sets/coronavirus-cases-UK-contry-region-local-authority-gov-uk.csv"))




;; Sub-sets of Gov.uk data
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Location specific data

(defn data-set-filter-locations
  [data-set location-set]
  (filter #(some location-set %)
          data-set))

(defn data-set-remove-locations
  [data-set location-set]
  (remove #(some location-set %)
          data-set))


;; Heading and local area district data
;; - passed to maximum-cases for sizing the scale of data
(def covid19-cases-uk-local-authority-district
  (data-set-remove-locations covid19-uk-england-combined-data
                             #{"Country" "Region"}))


(defn data-set-specific-date
  "Transform to map for visualization,
  including only the specific date.

  Use csv headings as keys in each map.

  Return: a sequence of maps"
  [extracted-data-set date]

  (let [heading (first extracted-data-set)]

    (semantic-csv/mappify
      {:keyify false}
      (conj
        (filter #(some #{date} %) extracted-data-set)
        heading))))


(def covid19-cases-uk-local-authority-district-date-specific
  (data-set-specific-date covid19-cases-uk-local-authority-district "2020-04-14"))


(def england-lad-geojson-with-cases-date-specific-lad
  (combine-data-sets geojson-england-local-area-district
                     covid19-cases-uk-local-authority-district-date-specific))
