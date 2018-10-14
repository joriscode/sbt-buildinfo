lazy val check = taskKey[Unit]("check")

lazy val commonSettings = Seq(
  version := "0.1",
  organization := "com.example",
  homepage := Some(url("http://example.com")),
  scalaVersion := "2.12.7"
)

lazy val root = (project in file(".")).
  aggregate(app).
  settings(commonSettings: _*)

lazy val app = (project in file("app")).
  enablePlugins(BuildInfoPlugin).
  settings(commonSettings: _*).
  settings(
    name := "sbt-buildinfo-example-app",
    buildInfoKeys := Seq(name,
                         projectID in LocalProject("root"),
                         version,
                         BuildInfoKey.map(homepage) { case (n, opt) => n -> opt.get },
                         scalaVersion),
    buildInfoPackage := "hello",
    check := {
      val f = (sourceManaged in Compile).value / "sbt-buildinfo" / ("%s.scala" format "BuildInfo")
      val lines = scala.io.Source.fromFile(f).getLines.toList
      lines match {
        case """package hello""" ::
             """""" ::
             """import scala.Predef._""" ::
             """""" ::
             """/** This object was generated by sbt-buildinfo. */""" ::
             """case object BuildInfo {""" ::
             """  /** The value is "sbt-buildinfo-example-app". */""" ::
             """  val name: String = "sbt-buildinfo-example-app"""" ::
             """  /** The value is "com.example:root:0.1". */""" ::
             """  val projectID: String = "com.example:root:0.1"""" ::
             """  /** The value is "0.1". */""" ::
             """  val version: String = "0.1"""" ::
             """  /** The value is new java.net.URL("http://example.com"). */""" ::
             """  val homepage = new java.net.URL("http://example.com")""" ::
             """  /** The value is "2.12.7". */""" ::
             """  val scalaVersion: String = "2.12.7"""" ::
             """  override val toString: String = {""" ::
             """    "name: %s, projectID: %s, version: %s, homepage: %s, scalaVersion: %s".format(""" ::
             """      name, projectID, version, homepage, scalaVersion""" ::
             """    )""" ::
             """  }""" ::
             """}""" :: Nil =>
        case _ => sys.error("unexpected output: " + lines.mkString("\n"))
      }
      ()
    }
  )

