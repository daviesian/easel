(ns easel.InnerRenderer
  (:use ;;[easel.AlgorithmInterfaces]
        [easel.opengl]
        [seesaw.core]
        [seesaw.graphics]
        [seesaw.color])
  (:import [java.util.prefs Preferences]
           [easel Algorithm2D Algorithm3D])

  (:gen-class
   :methods [#^{:static true} [init2D [int int easel.Algorithm2D] void]
             #^{:static true} [init3D [easel.Algorithm3D] void]
             #^{:static true} [setPixel [int int int int int] void]
             #^{:static true} [drawCube [javax.media.opengl.GL] void]]))

(defn put-pref [key value]
  (let [p (Preferences/userNodeForPackage easel.InnerRenderer)]
    (.put p key value)))

(defn get-pref [key default]
  (let [p (Preferences/userNodeForPackage easel.InnerRenderer)]
    (.get p key default)))

(def pixels (atom {}))
(def pix-width (atom 5))
(def pix-height (atom 5))

(def easel-frame (atom nil))
(def easel-canvas (atom nil))

(def run-algorithm (atom nil))
(def set-pixel-delay (atom (Integer/parseInt (get-pref "pixel-delay" "100"))))

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
        grid-size       (if (< grid-size 5) (Math/floor grid-size) grid-size)
        grid-size       (if (< grid-size 1) 1 grid-size)
        x-offset        (Math/floor (/ (- frame-width (* grid-size @pix-width)) 2))
        y-offset        (Math/floor (/ (- frame-height (* grid-size @pix-height)) 2))
        pixels          @pixels]
    (translate g x-offset y-offset)
    (doseq [[x y] (keys pixels)]
      (let [xT (* grid-size x)
            yT (* grid-size y)]
        (draw g
              (rect xT yT grid-size grid-size)
              (style :background (apply color (get pixels [x y]))))))
    (when (> grid-size 5)
      (doseq [x (range (+ 1 @pix-width))]
        (draw g
              (line (* grid-size x) 0 (* grid-size x) (* grid-size @pix-height))
              (style :foreground "black")))
      (doseq [y (range (+ 1 @pix-height))]
        (draw g
              (line 0 (* grid-size y) (* grid-size @pix-width) (* grid-size y))
              (style :foreground "black"))))
    ))




(defn init-frame-2d [width height on-close]
  (native!)
  (reset! pixels {})
  (reset! pix-width width)
  (reset! pix-height height)
  (reset! easel-canvas (canvas :paint draw-alg))
  (let [run-button   (button :action (action :name "Run" :handler (fn [e] (@run-algorithm))))


        speed-slider (slider :orientation :horizontal :min 0 :max 200 :value @set-pixel-delay :inverted? true)
        f            (frame :title "Easel 2D"
                            :content (border-panel :border 20
                                                   :north (border-panel :west run-button
                                                                        :center speed-slider)
                                                   :center @easel-canvas
                                                   :hgap 20)
                            :on-close on-close
                            :minimum-size [320 :by 240]
                            :size [(Integer/parseInt (get-pref "frame-width" "320"))
                                   :by
                                   (Integer/parseInt (get-pref "frame-height" "240"))])]
    (listen f :component-resized (fn [e] (let [size   (.getSize (.getComponent e))
                                              width  (int (.getWidth size))
                                              height (int (.getHeight size))]
                                          (put-pref "frame-width" (str width))
                                          (put-pref "frame-height" (str height)))))
    (listen speed-slider :change (fn [e]
                                   (reset! set-pixel-delay (config speed-slider :value))
                                   (put-pref "pixel-delay" (str @set-pixel-delay))))

    ;; (pack! f)
    (show! f)
    (reset! easel-frame f)))

(defn init-frame-3d [on-close]
  (native!)
  (let [canvas (gl-canvas)
        f (frame :title "Easel 3D"
                 :content (border-panel :border 20
                                        :center canvas)
                 :on-close on-close
                 :minimum-size [320 :by 240])]
    (pack! f)
    (show! f)))



(defn -init2D [width height alg]
  (init-frame-2d width height :exit)
  (reset! run-algorithm #(future (reset! pixels {})
                                 (.runAlgorithm alg width height))))


(defn setPixel [x y r g b]
  ;(println "Set pixel" x y r g b)
  (swap! pixels #(assoc % [x,y] [r g b]))
  (repaint! @easel-frame)
  (Thread/sleep @set-pixel-delay))

(defn -setPixel [x y r g b]
  (setPixel x y r g b))

(defn -init3D [alg]
  (init-frame-3d :exit)
  (reset! render-func #(.renderFrame alg %)))

(defn -drawCube [gl]
  (render-cube gl))
