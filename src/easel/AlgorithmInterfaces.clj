(ns easel.AlgorithmInterfaces)

(gen-interface
 :name easel.Algorithm2D
 :methods [[runAlgorithm [int int] void]])

(gen-interface
 :name easel.Algorithm3D
 :methods [[renderFrame [javax.media.opengl.GL2] void]])
