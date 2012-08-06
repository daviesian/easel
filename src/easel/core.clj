(ns easel.core
  (:use [easel.Renderer]
        [easel.opengl]
        [seesaw.core]
        [seesaw.graphics]
        [seesaw.color]))

;; State stuff

(do
  (init-frame-2d 100 50 :dispose)

  (defn my-algorithm [width height]
    (doseq [x (range width)]
      (doseq [y (range height)]
        (easel.Renderer/setPixel x y 255 0 0))))

  (reset! run-algorithm #(future
                           (reset! pixels {})
                           (my-algorithm @pix-width @pix-height) )))

#_(do
  (let [a (atom 0)]
    (reset! render-func
            (fn [gl]
              (render-axes gl)
              (gl-scale gl 4)
              (gl-rotate gl (* 2 @a) 1 0 0)
              (gl-rotate gl (* 3 @a) 0 1 0)
              (gl-rotate gl (* 5 @a) 0 0 1)
              (swap! a inc)
              (render-cube gl)
              )))
  (init-frame-3d :dispose))
