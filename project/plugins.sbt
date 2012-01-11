organization := "com.rgm"

//resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

resolvers += {
  Resolver.url("rgm", url("http://ivy.rgmadvisors.com:8081/artifactory/libs-snapshot-local"))(Resolver.ivyStylePatterns)
}

resolvers += Classpaths.typesafeResolver

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "0.11.2")
