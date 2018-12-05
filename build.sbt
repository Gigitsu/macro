organization := "underscore.io"

name := "scala-macros"

version := "1.0.0"

val commonSettings = Seq(
  scalaVersion := "2.12.6",
  scalacOptions ++= Seq("-deprecation", "-feature"),
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  )
)

lazy val helloLib        = project.in(file("hello/lib")).settings(commonSettings : _*)
lazy val hello           = project.in(file("hello/app")).settings(commonSettings : _*).dependsOn(helloLib)
