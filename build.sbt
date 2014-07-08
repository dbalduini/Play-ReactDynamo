name := "Play-ReactDynamo"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

organization := "io.react2"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full)

libraryDependencies ++= Seq(
  "com.typesafe.play"  %% "play"        % "2.2.3" % "provided",
  "com.typesafe.play"  %% "play-test"   % "2.2.3" % "test",
  "com.github.seratch" %% "awscala"     % "0.2.+",
  "org.scalamacros"    %% "quasiquotes" % "2.0.1"
)

resolvers += Resolver.sonatypeRepo("releases")

