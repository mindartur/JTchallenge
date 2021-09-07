# JTchallenge #

API rest which will return whether two “developers” are fully connected or not. 

Given a pair of developer handles they are considered connected if:
1) They follow each other on Twitter.
2) They have at least a Github organization in common.

## TODO:
To improve:
1) Improve test coverage: add unit tests for services (ConnectionService, GitHubService, TwitterService).
2) Improve error handling on the side of GitHubService and TwitterService. 
3) Maybe add identifiers validation for the Twitter and GitHub.
4) Add local cache for GitHubService and TwitterService.
5) Improve Dockerfile OR use sbt-native-plugin to create and publish docker image OR use sbt-assembly to create a binary
6) Add datadog tracing
7) Handle compile warnings

## Running the service locally
1) Create an `.env` file using the `.env.template`
2) Build Docker image: `docker build -t jt-challenge .`
3) Run Docker container: `docker run --env-file .env -p 8080:8080 --network host jt-challenge:latest`

## Swagger
Assess Swagger docs by `http://127.0.0.1:8080/docs/index.html?url=/docs/docs.yaml`.

(For some reason redirect for `http://127.0.0.1:8080/docs` doesn't always work in Firefox)

## System requirements ##

- Java 11
- sbt

## Developer guide ##

For development and testing you need to install [sbt](http://www.scala-sbt.org/).
Please see [CONTRIBUTING.md](JTchallenge/CONTRIBUTING.mdTRIBUTING.md) for details how to to contribute
to the project.

During development you can start (and restart) the application via the `reStart`
sbt task provided by the sbt-revolver plugin.

### Tests ###

Tests are included in the project. You can run them via the appropriate sbt tasks
`test` and `IntegrationTest/test`. The latter will execute the integration tests.


#### Manual test
Run GET request on this URL in order to get positive result:
```http://127.0.0.1:8080/developers/connected/ghostdogpr/jdegoes/```

Expected JSON:
```json
{
  "connected": true,
  "organisations": [
    "zio"
  ]
}
```

## Deployment guide ##


## License ##

This code is licensed under the Mozilla Public License Version 2.0, see the
[LICENSE](JTchallenge/LICENSEenge/LICENSE) file for details.
