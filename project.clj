(defproject easel "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :repositories {"local" ~(str (.toURI (java.io.File. "repo")))}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [seesaw "1.4.2"]
                 [local/jogl.all.cl "1.0.0"]]

  :aot [easel.RunnableGraphicsAlgorithm
        easel.Renderer]
  )
