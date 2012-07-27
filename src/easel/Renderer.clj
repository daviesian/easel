(ns easel.Renderer
  (:use [easel.RunnableGraphicsAlgorithm]
        [seesaw.core]
        [seesaw.graphics]
        [seesaw.color])
  (:import [javax.media.opengl.awt GLCanvas]
           [javax.media.opengl GLEventListener])
  (:gen-class
   :methods [#^{:static true} [initialise [int int easel.RunnableGraphicsAlgorithm] void]
             #^{:static true} [setPixel [int int int int int] void]]))

(def pixels (atom {}))
(def pix-width (atom 5))
(def pix-height (atom 5))

(def easel-frame (atom nil))
(def easel-canvas (atom nil))

(def run-algorithm (atom nil))
(def set-pixel-delay (atom 100))

(defn get-canvas-width []
  (int (.getWidth (config @easel-canvas :size))))
(defn get-canvas-height []
  (int (.getHeight (config @easel-canvas :size))))

(defn draw-alg [c g]
  (anti-alias g)
  (let [frame-width     (get-canvas-width)
        frame-height    (get-canvas-height)
        max-cell-width  (float (/ (- frame-width 1) @pix-width))
        max-cell-height (float (/ (- frame-height 1) @pix-height))
        grid-size       (Math/min max-cell-width max-cell-height)
        x-offset        (/ (- frame-width (* grid-size @pix-width)) 2)
        y-offset        (/ (- frame-height (* grid-size @pix-height)) 2)
        pixels          @pixels]
    (translate g x-offset y-offset)
    (doseq [[x y] (keys pixels)]
      (let [xT (* grid-size x)
            yT (* grid-size y)]
        (draw g
              (rect xT yT grid-size grid-size)
              (style :background (apply color (get pixels [x y]))))))
    (doseq [x (range (+ 1 @pix-width))]
      (draw g
            (line (* grid-size x) 0 (* grid-size x) (* grid-size @pix-height))
            (style :foreground "black")))
    (doseq [y (range (+ 1 @pix-height))]
      (draw g
            (line 0 (* grid-size y) (* grid-size @pix-width) (* grid-size y))
            (style :foreground "black")))
    ))

(def listener
  (reify GLEventListener
    (display [this drawable]
      (let [gl (-> drawable .getGL .getGL2)]
        (.glBegin gl javax.media.opengl.GL/GL_TRIANGLES)
        (.glColor3f gl 1 0 0)
        (.glVertex2f gl -1 -1)
        (.glVertex2f gl 0 1)
        (.glVertex2f gl 1 -1)
        (.glEnd gl)))
    (init [this drawable])
    (dispose [this drawable])
    (reshape [this drawable x y width height])))

(defn init-frame [width height on-close]
  (native!)
  (reset! pixels {})
  (reset! pix-width width)
  (reset! pix-height height)
  (reset! easel-canvas (canvas :paint draw-alg))
  (let [run-button   (button :action (action :name "Run" :handler (fn [e] (@run-algorithm))))
        gl-canvas    (GLCanvas.)
        speed-slider (slider :orientation :horizontal :min 0 :max 200 :value @set-pixel-delay :inverted? true)
        f            (frame :title "Easel"
                            :content (border-panel :border 20
                                                   :north (border-panel :west run-button
                                                                        :center speed-slider)
                                                   :center @easel-canvas
                                                   :south (border-panel :size [640 :by 480]
                                                                        :center gl-canvas)
                                                   :hgap 20)
                            :on-close on-close
                            :minimum-size [320 :by 240])]
    (listen speed-slider :change (fn [e] (reset! set-pixel-delay (config speed-slider :value))))
    (.addGLEventListener gl-canvas listener)
    (pack! f)
    (show! f)
    (reset! easel-frame f)))




(defn -initialise [width height alg]
  (println "Init with" alg)
  (println (.getResource (ClassLoader/getSystemClassLoader) ""))
  (init-frame width height :exit)
  (reset! run-algorithm #(future (reset! pixels {})
                                 (.runAlgorithm alg width height))))


(defn setPixel [x y r g b]
  ;(println "Set pixel" x y r g b)
  (swap! pixels #(assoc % [x,y] [r g b]))
  (repaint! @easel-frame)
  (Thread/sleep @set-pixel-delay))

(defn -setPixel [x y r g b]
  (setPixel x y r g b))
