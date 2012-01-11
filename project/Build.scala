import sbt._
import Keys._
import xml.NodeSeq

object BuildSettings {
  val exclusions = Seq("javax", "jmxri", "jmxtools", "mail", "jms")
  val ivyExclude = <dependencies>{exclusions.map ( e => <exclude module={e}/>)}</dependencies>

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := "kafka",
    version      := "0.7.0",
//    crossScalaVersions := Seq("2.8.1", "2.9.1"),
    crossScalaVersions := Seq("2.9.1"),
    scalacOptions ++= Seq("-deprecation", "-unchecked"),
    crossPaths := true,
    ivyXML := ivyExclude
  )

  def buildPomExtra(pom: NodeSeq, name: String, desc: String) = {
    pom ++ Seq(
      <name>
        {name}
      </name>,
      <description>
        {desc}
      </description>,
      <url>http://incubator.apache.org/kafka</url>,
      <licenses>
        <license>
          <name>Apache</name>
          <url>http://svn.apache.org/repos/asf/incubator/kafka/trunk/LICENSE</url>
          <distribution>repo</distribution>
        </license>
      </licenses>,
      <scm>
        <url>http://svn.apache.org/repos/asf/incubator/kafka/trunk/</url>
        <connection>scm:svn:http://svn.apache.org/repos/asf/incubator/kafka/trunk</connection>
        <developerConnection>scm:svn:https://foo.googlecode.com/svn/trunk/</developerConnection>
        <connection>scm:git:git://github.com/linkedin-sna/norbert.git</connection>
      </scm>,
      <developers>
        <developer>
          <id>jkreps</id>
          <name>Jay Kreps</name>
          <url>http://www.linkedin.com/in/jaykreps</url>
        </developer>
        <developer>
          <id>junrao</id>
          <name>Jun Rao</name>
          <url>http://www.linkedin.com/in/junrao</url>
        </developer>
        <developer>
          <id>nehanarkhede</id>
          <name>Joshua Hartman</name>
          <url>http://www.linkedin.com/in/nehanarkhede</url>
        </developer>
      </developers>
    )
  }
}

object Resolvers {
  val oracleRepo = "Oracle Maven 2 Repository" at "http://download.oracle.com/maven"
  val jBossRepo = "JBoss Maven 2 Repository" at "http://repository.jboss.com/maven2"
  val repo1 = "Maven main repo" at "http://repo1.maven.org/maven2"
  val kafkaResolvers = Seq(oracleRepo, jBossRepo, repo1)
}

object CoreDependencies {
//  TODO jhartman: When sbt 0.11.1 is ready, we can use the following code instead of ivy xml
  val exclusions = Seq("javax", "jmxri", "jmxtools", "mail", "jms") map (n => ExclusionRule(name = n))
  val log4j = ("log4j" % "log4j" % "1.2.15") excludeAll (exclusions :_*)
  val jopt = "net.sf.jopt-simple" % "jopt-simple" % "3.2"
  val zookeeper = "org.apache.zookeeper" % "zookeeper" % "3.3.3"
  val zkclient = "com.github.sgroschupf" % "zkclient" % "0.1"
  val snappy = "org.xerial.snappy" % "snappy-java" % "1.0.4.1"
  val deps = Seq(log4j, jopt, zookeeper, zkclient, snappy)
}

object HadoopProducerDependencies {
  val avro = "org.apache.avro" % "avro" % "1.4.1"
  val jacksonCore = "org.codehaus.jackson" % "jackson-core-asl" % "1.5.5"
  val jacksonMapper = "org.codehaus.jackson" % "jackson-mapper-asl" % "1.5.5"
  val deps = Seq(avro, jacksonCore, jacksonMapper)
}

object HadoopConsumerDependencies {
  val jodaTime = "joda-time" % "joda-time" % "1.6"
  val httpclient = "commons-httpclient" % "commons-httpclient" % "3.1"
  val deps = Seq(jodaTime, httpclient)
}

object TestDependencies {
  val easymock = "org.easymock" % "easymock" % "3.0" % "test"
  val junit = "junit" % "junit" % "4.1" % "test"
  val scalaTest = "org.scalatest" % "scalatest" % "1.2" % "test"
  val deps = Seq(easymock, junit, scalaTest)
}

object KafkaBuild extends Build {
  import BuildSettings._
  lazy val core = Project("core-kafka", file("core"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= CoreDependencies.deps ++ TestDependencies.deps,
      resolvers := Resolvers.kafkaResolvers
    )
  )

  lazy val examples = Project("examples", file("examples"),
   settings = buildSettings
  ) dependsOn (core)

  lazy val perf = Project("perf", file("perf"),
    settings = buildSettings
  ) dependsOn (core)

  lazy val hadoopProducer = Project("hadoop-producer", file("hadoop-producer"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= HadoopProducerDependencies.deps
    )
  ) dependsOn (core)

  lazy val hadoopConsumer = Project("hadoop-consumer", file("hadoop-consumer"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= HadoopConsumerDependencies.deps
    )
  ) dependsOn (core)

  lazy val contrib = Project("contrib", file("contrib"), settings = buildSettings) aggregate(hadoopConsumer, hadoopProducer)

  lazy val root = Project("root", file("."),
    settings = buildSettings ++ Seq(
      pomExtra <<= (pomExtra, name, description) { buildPomExtra }
  )) aggregate(core, examples, perf, contrib)

  lazy val full = Project(
    id           = "kafka",
    base         = file("full"),
    settings     = buildSettings ++ Seq(
      description := "Includes all of kafka project in one",
      libraryDependencies ++= CoreDependencies.deps ++ TestDependencies.deps ++ HadoopProducerDependencies.deps ++ HadoopConsumerDependencies.deps,

      (unmanagedJars in Compile) <<= (projects.map(unmanagedJars in Compile in _).join).map(_.flatten),
      (unmanagedSourceDirectories in Compile) <<= projects.map(unmanagedSourceDirectories in Compile in _).join.apply(_.flatten),
      (managedSourceDirectories in Compile) <<= projects.map(managedSourceDirectories in Compile in _).join.apply(_.flatten),

      pomExtra <<= (pomExtra, name, description) { buildPomExtra }
    )
  )
}
