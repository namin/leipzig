(ns whelmed.songs.west
  (:use
    [whelmed.scale]
    [whelmed.canon]
    [whelmed.melody]
    [whelmed.instrument]
    [overtone.live :only [midi->hz now stop]]))

(defn => [value & fs] (reduce #(%2 %1) value fs))
(defn lower [f] (comp #(- % 7) f))
(def progression (map #(map % seventh) [i (lower v) (lower vi) (lower iii)]))

(def backing
    (map
      #(map (partial vector %1) %2 (repeat 4))
      [0 4 8 12]
      progression))

(defn after [wait] (shift [wait 0 0])) 

(defn follow [first gap second]
    (let [[timing _ duration] (last first)
                  shifted ((after (+ duration gap timing)) second)]
          (concat first shifted))) 

(def ill-run-away [[-1/2 3 1/2] [0 4 1/4] [1/4 3 1/4] [1/2 4 1/2]])
(def ill-get-away (assoc ill-run-away 2 [1/4 6 1/4]))
(def my-heart-will-go-west-with-the-sun
  [[-1/2 3 1/2]
   [0 4 3/4] [3/4 3 3/4] [3/2 2 1/4]
   [8/4 4 3/4] [11/4 3 1/4] [14/4 2 1/4]
   [15/4 -1 17/4]
   ])

(def a-parting-kiss [[0 4 1/4] [1/4 3 1/4] [1/2 4 1/4] [3/4 6 1/4] [6/4 4 10/4]])
(def like-fairy-floss (cons [-1/4 3 1/4] a-parting-kiss))
(def dissolves-on-the-tip-of-my-tongue [[-1/4 4 1/4] [0 6 1/2] [3/4 4 3]])

(defn west#
  [tempo scale parts]
  (let [start (+ (now) 500)
        in-time (comp (skew timing tempo) (skew duration tempo))
        in-key (skew pitch scale)
        midify (fn [instrument#] (comp instrument# midi->hz))
        play-now# #(=> %2 in-time (after start) in-key (partial play-on# (midify %1)))]
    (dorun (map (partial apply play-now#) parts))))

(def theme (-> ill-run-away
             (follow 3 ill-get-away)
             (follow 3 my-heart-will-go-west-with-the-sun)))

(def reply (-> a-parting-kiss
             (follow 0 like-fairy-floss)
             (follow 0 dissolves-on-the-tip-of-my-tongue)
             (follow 0 dissolves-on-the-tip-of-my-tongue)))

(defn times [phrase n] (reduce #(follow %1 0 %2) (repeat n phrase))) 
(def melody (follow (times theme 2) 0 (times reply 2)))

(def accompaniment (times (apply concat backing) 4))
(def bass ((shift [0 -7 0]) (times (map first backing) 4)))

(comment
(west# (bpm 80) (comp E aeolian)
      {sawish# (times theme 2) 
       sinish# ((shift [32 0 0]) (times reply 2))
       groan# bass
       shudder# accompaniment})
  )