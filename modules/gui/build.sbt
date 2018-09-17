name := "jatos-gui"
version := "3.3.2"
organization := "org.jatos"
scalaVersion := "2.11.11"

includeFilter in(Assets, LessKeys.less) := "*.less"

excludeFilter in(Assets, LessKeys.less) := "_*.less"

libraryDependencies ++= Seq(
  javaCore,
  javaJdbc,
  javaJpa,
  javaWs,
  cache,
  "org.webjars" % "bootstrap" % "3.3.4",
  "org.pac4j" % "play-pac4j" % "3.0.1",
  "org.pac4j" % "pac4j-http" % "2.0.0",
  "org.pac4j" % "pac4j-cas" % "2.0.0",
  "org.pac4j" % "pac4j-openid" % "2.0.0" exclude("xml-apis" , "xml-apis"),
  "org.pac4j" % "pac4j-oauth" % "2.0.0",
  "org.pac4j" % "pac4j-saml" % "2.0.0",
  "org.pac4j" % "pac4j-oidc" % "2.0.0" exclude("commons-io" , "commons-io"),
  "org.pac4j" % "pac4j-gae" % "2.0.0",
  "org.pac4j" % "pac4j-jwt" % "2.0.0" exclude("commons-io" , "commons-io"),
  "org.pac4j" % "pac4j-ldap" % "2.0.0",
  "org.pac4j" % "pac4j-sql" % "2.0.0",
  "org.pac4j" % "pac4j-mongo" % "2.0.0",
  "org.pac4j" % "pac4j-stormpath" % "2.0.0"
)

// Routes from submodules
routesGenerator := InjectedRoutesGenerator

// No source docs in distribution 
sources in(Compile, doc) := Seq.empty

// No source docs in distribution 
publishArtifact in(Compile, packageDoc) := false
