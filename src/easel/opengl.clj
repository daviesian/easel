(ns easel.opengl
  (:import [javax.media.opengl.awt GLCanvas]
           [javax.media.opengl DebugGL2 GL GL2 GLProfile GLCapabilities GLEventListener]
           [javax.media.opengl.glu GLU]
           [com.jogamp.opengl.util Animator FPSAnimator]))

(defmacro gl-vertex-group [gl mode & body]
  `(do
     (.glBegin ~gl ~mode)
     ~@body
     (.glEnd ~gl)))

(defn generate-gl-caps []
  (let [gl-caps      (GLCapabilities. (GLProfile/getDefault))]
    (.setSampleBuffers gl-caps true)
    (.setNumSamples gl-caps 2)
    (.setDoubleBuffered gl-caps true)
    gl-caps))

(defmacro local-transform [gl & body]
  `(do
     (.glPushMatrix ~gl)
     ~@body
     (.glPopMatrix ~gl)
     ))

(defn gl-rotate [gl angle x y z]
  (.glRotatef gl (float angle) (float x) (float y) (float z)))

(defn gl-scale
  ([gl scale]
     (.glScalef gl (float scale) (float scale) (float scale)))
  ([gl x y z]
     (.glScalef gl (float x) (float y) (float z))))

(defn gl-clear [gl r g b a & buffers]
  (.glClearColor gl (float r) (float g) (float b) (float a))
  (.glClear gl (apply bit-or buffers)))

(def render-func (atom (fn [gl])))

(defn reset-camera [gl]
  (let [glu (GLU.)]
    (.glMatrixMode gl GL2/GL_PROJECTION)
    (.glLoadIdentity gl)
    (.glMatrixMode gl GL2/GL_MODELVIEW)
    (.glLoadIdentity gl)
    ))


(def viewport-width (atom 1))
(def viewport-height (atom 1))

(defn set-camera [gl]
  (let [glu (GLU.)]

    (.glMatrixMode gl GL2/GL_PROJECTION)
    (.glLoadIdentity gl)
    (.gluPerspective glu (float 45) (float (/ @viewport-width @viewport-height)) (float 0.01) (float 1000))
    (.gluLookAt glu
                (float 0) (float 0) (float 10)
                (float 0) (float 0) (float 0)
                (float 0) (float 1) (float 0))
    (.glMatrixMode gl GL2/GL_MODELVIEW)
    (.glLoadIdentity gl)
    ))

(defn render-fixed-background [gl]
  (reset-camera gl)
  #_(gl-vertex-group gl GL/GL_TRIANGLES
    (.glColor4f gl 1 0 0 0.1)
    (.glVertex2f gl -1 -1)
    (.glVertex2f gl 0 1)
    (.glVertex2f gl 1 -1)
    (.glColor4f gl 0 1 0 0.1)
    (.glVertex2f gl -1 1)
    (.glVertex2f gl 0 -1)
    (.glVertex2f gl 1 1)))

(defn render-axes [gl]
  (.glLineWidth gl 1)
  (let [len 10]
    (gl-vertex-group gl GL/GL_LINES
      (.glColor4f gl 1 1 1 0.8)
      (.glVertex3i gl (- len) 0 0)
      (.glVertex3i gl len 0 0)

      (.glVertex3i gl 0 (- len) 0)
      (.glVertex3i gl 0 len 0)
      )))
(defn render-cube [gl]

  (local-transform gl
    (gl-scale gl 0.5)
    (gl-vertex-group gl GL2/GL_QUADS
                                        ; Front
      (.glColor4f gl 1 0 0 0.6)
      (.glVertex3i gl -1 -1 1)
      (.glVertex3i gl 1 -1 1)
      (.glVertex3i gl 1 1 1)
      (.glVertex3i gl -1 1 1)
                                        ; Left
      (.glColor4f gl 0 1 0 0.6)
      (.glVertex3i gl -1 -1 1)
      (.glVertex3i gl -1 1 1)
      (.glVertex3i gl -1 1 -1)
      (.glVertex3i gl -1 -1 -1)
                                        ; Right
      (.glColor4f gl 0 0 1 0.6)
      (.glVertex3i gl 1 -1 1)
      (.glVertex3i gl 1 -1 -1)
      (.glVertex3i gl 1 1 -1)
      (.glVertex3i gl 1 1 1)
                                        ; Back
      (.glColor4f gl 1 1 0 0.6)
      (.glVertex3i gl 1 -1 -1)
      (.glVertex3i gl -1 -1 -1)
      (.glVertex3i gl -1 1 -1)
      (.glVertex3i gl 1 1 -1)
                                        ; Top
      (.glColor4f gl 0 1 1 0.6)
      (.glVertex3i gl -1 1 1)
      (.glVertex3i gl 1 1 1)
      (.glVertex3i gl 1 1 -1)
      (.glVertex3i gl -1 1 -1)
                                        ; Bottom
      (.glColor4f gl 1 0 1 0.6)
      (.glVertex3i gl -1 -1 1)
      (.glVertex3i gl -1 -1 -1)
      (.glVertex3i gl 1 -1 -1)
      (.glVertex3i gl 1 -1 1))))

(def bg-color (atom {:r 0 :g 0 :b 0}))

(def listener
  (reify GLEventListener
    (display [this drawable]
      (try
        (let [gl (-> drawable .getGL .getGL2)]
          (gl-clear gl (:r @bg-color) (:g @bg-color) (:b @bg-color) 1 GL/GL_COLOR_BUFFER_BIT GL/GL_DEPTH_BUFFER_BIT)
          (set-camera gl)
          (.glEnable gl GL/GL_DEPTH_TEST)
          (@render-func gl)
          (.glDisable gl GL/GL_DEPTH_TEST)
          (render-fixed-background gl))
        (catch Exception e
          (println e))))
    (init [this drawable]
      (let [gl (-> drawable .getGL .getGL2)]
        (.setGL drawable (DebugGL2. gl))
        (.glEnable gl GL/GL_BLEND)
        (.glBlendFunc gl GL/GL_SRC_ALPHA GL/GL_ONE_MINUS_SRC_ALPHA)
        (.glDepthFunc gl GL/GL_LEQUAL)
        (.glShadeModel gl GL2/GL_SMOOTH))
      (let [bg (bean (.getBackground drawable))]
        (reset! bg-color {:r (float (/ (:red bg) 256)) :g (float (/ (:green bg) 256)) :b (float (/ (:blue bg) 256))})))
    (dispose [this drawable])
    (reshape [this drawable x y width height]
      (.glViewport (.getGL drawable) 0 0 width height)
      (reset! viewport-width width)
      (reset! viewport-height height)
      )))

(defn gl-canvas []
  (let [canvas (GLCanvas. (generate-gl-caps))]
    (.addGLEventListener canvas listener)
    (let [a (FPSAnimator. canvas 60)]
      (.add a canvas)
      (.start a))
    canvas))
