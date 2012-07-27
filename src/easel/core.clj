(ns easel.core
  (:use [easel.Renderer]
        [seesaw.core]
        [seesaw.graphics]
        [seesaw.color]))

;; State stuff

(init-frame 10 5 :dispose)

;; This would be in Java somehow
(defn my-algorithm [width height]
  (doseq [x (range width)]
    (doseq [y (range height)]
      (easel.Renderer/setPixel x y 255 0 0))))

(reset! run-algorithm #(future
                         (reset! pixels {})
                         (my-algorithm @pix-width @pix-height) ))
