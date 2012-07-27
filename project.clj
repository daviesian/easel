(defproject easel "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :repositories {"local" ~(str (.toURI (java.io.File. "maven_repo")))}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [seesaw "1.4.2"]
                 [jogl/jogl "0.0.1"]]

  :aot [easel.RunnableGraphicsAlgorithm
        easel.Renderer]
  )
