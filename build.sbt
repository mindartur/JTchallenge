// *****************************************************************************
// Projects
// *****************************************************************************

lazy val jtchallenge =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .configs(IntegrationTest)
    .settings(
      name := "jt-challenge"
    )
    .settings(settings)
    .settings(
      Defaults.itSettings,
      headerSettings(IntegrationTest),
      inConfig(IntegrationTest)(scalafmtSettings),
      IntegrationTest / console / scalacOptions --= Seq(
        "-Xfatal-warnings",
        "-Ywarn-unused-import",
        "-Ywarn-unused:implicits",
        "-Ywarn-unused:imports",
        "-Ywarn-unused:locals",
        "-Ywarn-unused:params",
        "-Ywarn-unused:patvars",
        "-Ywarn-unused:privates"
      ),
      IntegrationTest / parallelExecution := false
    )
    .settings(
      resolvers += Resolver.sonatypeRepo("releases"),
      libraryDependencies ++= Seq(
        library.catsCore,
        library.circeCore,
        library.circeGeneric,
        library.circeRefined,
        library.circeParser,
        library.enumeratumCirce,
        library.github4s,
        library.http4sCirce,
        library.http4sDsl,
        library.http4sEmberClient,
        library.http4sEmberServer,
        library.logback,
        library.pureConfig,
        library.refinedCats,
        library.refinedCore,
        library.refinedPureConfig,
        library.tapirCats,
        library.tapirCirce,
        library.tapirCore,
        library.tapirEnumeratum,
        library.tapirHttp4s,
        library.tapirOpenApiDocs,
        library.tapirOpenApiYaml,
        library.tapirRefined,
        library.tapirSwaggerUi,
        library.twitter4s,
        library.munit             % IntegrationTest,
        library.munitCatsEffect   % IntegrationTest,
        library.munitScalaCheck   % IntegrationTest,
        library.refinedScalaCheck % IntegrationTest,
        library.scalaCheck        % IntegrationTest,
        library.munit             % Test,
        library.munitCatsEffect   % Test,
        library.munitScalaCheck   % Test,
        library.refinedScalaCheck % Test,
        library.scalaCheck        % Test
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val cats            = "2.6.1"
      val circe           = "0.14.1"
      val enumeratum      = "1.7.0"
      val github4s        = "0.29.1"
      val http4s          = "0.23.0"
      val logback         = "1.2.5"
      val munit           = "0.7.29"
      val munitCatsEffect = "1.0.5"
      val pureConfig      = "0.16.0"
      val refined         = "0.9.27"
      val scalaCheck      = "1.15.4"
      val tapir           = "0.19.0-M4"
      val twitter4s       = "7.0"
    }
    val catsCore          = "org.typelevel"               %% "cats-core"                % Version.cats
    val circeCore         = "io.circe"                    %% "circe-core"               % Version.circe
    val circeGeneric      = "io.circe"                    %% "circe-generic"            % Version.circe
    val circeRefined      = "io.circe"                    %% "circe-refined"            % Version.circe
    val circeParser       = "io.circe"                    %% "circe-parser"             % Version.circe
    val enumeratumCirce   = "com.beachape"                %% "enumeratum-circe"         % Version.enumeratum
    val github4s          = "com.47deg"                   %% "github4s"                 % Version.github4s // exclude("org.http4s", "http4s-circe_2.13") exclude("org.http4s", "http4s-client_2.13")
    val http4sCirce       = "org.http4s"                  %% "http4s-circe"             % Version.http4s
    val http4sDsl         = "org.http4s"                  %% "http4s-dsl"               % Version.http4s
    val http4sEmberServer = "org.http4s"                  %% "http4s-ember-server"      % Version.http4s
    val http4sEmberClient = "org.http4s"                  %% "http4s-ember-client"      % Version.http4s
    val logback           = "ch.qos.logback"              %  "logback-classic"          % Version.logback
    val munit             = "org.scalameta"               %% "munit"                    % Version.munit
    val munitCatsEffect   = "org.typelevel"               %% "munit-cats-effect-3"      % Version.munitCatsEffect
    val munitScalaCheck   = "org.scalameta"               %% "munit-scalacheck"         % Version.munit
    val pureConfig        = "com.github.pureconfig"       %% "pureconfig"               % Version.pureConfig
    val refinedCore       = "eu.timepit"                  %% "refined"                  % Version.refined
    val refinedCats       = "eu.timepit"                  %% "refined-cats"             % Version.refined
    val refinedPureConfig = "eu.timepit"                  %% "refined-pureconfig"       % Version.refined
    val refinedScalaCheck = "eu.timepit"                  %% "refined-scalacheck"       % Version.refined
    val scalaCheck        = "org.scalacheck"              %% "scalacheck"               % Version.scalaCheck
    val tapirCats         = "com.softwaremill.sttp.tapir" %% "tapir-cats"               % Version.tapir
    val tapirCirce        = "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % Version.tapir
    val tapirCore         = "com.softwaremill.sttp.tapir" %% "tapir-core"               % Version.tapir
    val tapirEnumeratum   = "com.softwaremill.sttp.tapir" %% "tapir-enumeratum"         % Version.tapir
    val tapirHttp4s       = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"      % Version.tapir
    val tapirOpenApiDocs  = "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % Version.tapir
    val tapirOpenApiYaml  = "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % Version.tapir
    val tapirRefined      = "com.softwaremill.sttp.tapir" %% "tapir-refined"            % Version.tapir
    val tapirSwaggerUi    = "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s"  % Version.tapir
    val twitter4s         = "com.danielasfregola"         %% "twitter4s"                % Version.twitter4s
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
  scalafmtSettings ++
  scoverageSettings

lazy val commonSettings =
  Seq(
    scalaVersion := "2.13.6",
    organization := "com.challenge",
    organizationName := "Artur",
    startYear := Some(2021),
    licenses += ("MPL-2.0", url("https://www.mozilla.org/en-US/MPL/2.0/")),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    testFrameworks += new TestFramework("munit.Framework"),
    Test / parallelExecution := false,
    scalacOptions ++= Seq(
      "-deprecation",
      "-explaintypes",
      "-feature",
      "-language:higherKinds",
      "-unchecked",
      "-Xcheckinit",
      //"-Xfatal-warnings", // Should be enabled if feasible.
      "-Xlint:adapted-args",
      "-Xlint:constant",
      "-Xlint:delayedinit-select",
      "-Xlint:doc-detached",
      "-Xlint:inaccessible",
      "-Xlint:infer-any",
      "-Xlint:missing-interpolator",
      "-Xlint:nullary-unit",
      "-Xlint:option-implicit",
      "-Xlint:package-object-classes",
      "-Xlint:poly-implicit-overload",
      "-Xlint:private-shadow",
      "-Xlint:stars-align",
      "-Xlint:type-parameter-shadow",
      "-Yrangepos", // Needed to make `clue` work correctly (munit).
      "-Ywarn-dead-code",
      "-Ywarn-extra-implicit",
      "-Ywarn-numeric-widen",
      "-Ywarn-unused:implicits",
      "-Ywarn-unused:imports",
      "-Ywarn-unused:locals",
      "-Ywarn-unused:params",
      "-Ywarn-unused:patvars",
      "-Ywarn-unused:privates",
      "-Ywarn-value-discard",
      "-Ycache-plugin-class-loader:last-modified",
      "-Ycache-macro-class-loader:last-modified",
    ),
    Compile / console / scalacOptions --= Seq(
      "-Xfatal-warnings",
      "-Ywarn-unused-import",
      "-Ywarn-unused:implicits",
      "-Ywarn-unused:imports",
      "-Ywarn-unused:locals",
      "-Ywarn-unused:params",
      "-Ywarn-unused:patvars",
      "-Ywarn-unused:privates"
    ),
    Compile / compile / wartremoverWarnings ++= Warts.unsafe.filterNot(_ == Wart.Any), // Disable the "Any" wart due to too many false positives.
    Test / console / scalacOptions --= Seq(
      "-Xfatal-warnings",
      "-Ywarn-unused-import",
      "-Ywarn-unused:implicits",
      "-Ywarn-unused:imports",
      "-Ywarn-unused:locals",
      "-Ywarn-unused:params",
      "-Ywarn-unused:patvars",
      "-Ywarn-unused:privates"
    )
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := false,
  )

lazy val scoverageSettings =
  Seq(
    coverageMinimum := 60,
    coverageFailOnMinimum := false,
    coverageHighlighting := true
  )

