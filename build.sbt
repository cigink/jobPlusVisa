name := "stackcrawler"

version := "0.1"

scalaVersion := "2.13.2"

libraryDependencies += "com.rometools" % "rome" % "1.8.1"
libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.3.2"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.24"
libraryDependencies += "org.jsoup" % "jsoup" % "1.13.1"
libraryDependencies +="org.apache.commons" % "commons-dbcp2" % "2.0.1"

enablePlugins(JavaAppPackaging)


