lazy val check = taskKey[Unit]("checks this plugin")

lazy val root = (project in file(".")).
  enablePlugins(BuildInfoPlugin).
  settings(
    name := "helloworld",
    version := "0.1",
    scalaVersion := "2.10.2",
    buildInfoKeys := Seq(
      name,
      scalaVersion
    ),
    buildInfoPackage := "hello",
    buildInfoOptions ++= Seq(
      BuildInfoOption.ToJson,
      BuildInfoOption.ToMap,
      BuildInfoOption.Traits("TestTrait1", "TestTrait2"),
      BuildInfoOption.Traits("TestTrait3")),
    homepage := Some(url("http://example.com")),
    licenses := Seq("MIT License" -> url("https://github.com/sbt/sbt-buildinfo/blob/master/LICENSE")),
    check := {
      val f = (sourceManaged in Compile).value / "sbt-buildinfo" / ("%s.scala" format "BuildInfo")
      val lines = scala.io.Source.fromFile(f).getLines.toList
      lines match {
        case """package hello""" ::
             """""" ::
             """import scala.Predef._""" ::
             """""" ::
             """/** This object was generated by sbt-buildinfo. */""" ::
             """case object BuildInfo extends TestTrait1 with TestTrait2 with TestTrait3 {""" ::
             """  /** The value is "helloworld". */"""::
             """  val name: String = "helloworld"""" ::
             """  /** The value is "2.10.2". */""" ::
             """  val scalaVersion: String = "2.10.2"""" ::
             """  override val toString: String = {""" ::
             """    "name: %s, scalaVersion: %s" format (""" ::
             """      name, scalaVersion""" ::
             """    )""" ::
             """  }""" ::
             """  val toMap: Map[String, Any] = Map[String, Any](""" ::
             """    "name" -> name,""" ::
             """    "scalaVersion" -> scalaVersion)""" ::
             """""" ::
             """  val toJson: String = toMap.map{ i =>""" ::
             """    def quote(x:Any) : String = "\"" + x + "\""""" ::
             """    val key : String = quote(i._1)""" ::
             """    val value : String = i._2 match {""" ::
             """       case elem : Seq[_] => elem.map(quote).mkString("[", ",", "]")""" ::
             """       case elem : Option[_] => elem.map(quote).getOrElse("null")""" ::
             """       case elem => quote(elem)""" ::
             """    }""" ::
             """    s"$key : $value"""" ::
             """    }.mkString("{", ", ", "}")""" ::
             """}""" :: Nil =>
        case _ => sys.error("unexpected output: \n" + lines.mkString("\n"))
      }
      ()
    }
  )
