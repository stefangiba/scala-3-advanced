val scala3Version = "3.3.0"

lazy val root = project
  .in(file("."))
  .settings(
    name         := "scala-3-advanced",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.3"
    )
  )
