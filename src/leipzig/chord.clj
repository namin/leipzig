(ns leipzig.chord
 (:require [clojure.set :as set]))

(defn- from [n] (partial + n))
(defn- update-all [m [k & ks] f]
 (if k
   (-> m (update-in [k] f) (update-all ks f))
   m))
(defn- mapval [m f] (update-all m (keys m) f))

(defn root
  "Translates a chord so that its root is at tonic.
  e.g. (-> triad (root 4))" 
  [chord tonic] (-> chord (mapval (from tonic)))) 

(def triad
  "A three-tone chord."
  {:i 0, :iii 2, :v 4})

(def seventh 
  "A four-tone chord."
  (-> triad (assoc :vii 6)))

(def ninth 
  "A five-tone chord."
  (-> seventh (assoc :ix 8)))

(defn inversion [chord n]
  "Drops all but the first n tones of the chord.
  e.g. (-> triad (inversion 2))"
  (let [stable (->> [:i :iii :v] (take n) set) 
        lowered (set/difference (-> chord keys set) stable)]
    (update-all chord (seq lowered) (from -7))))
