# Hello World Vaadin

[![CI](https://github.com/denisw160/HelloWorldVaadin/actions/workflows/ci.yml/badge.svg)](https://github.com/denisw160/HelloWorldVaadin/actions/workflows/ci.yml)
[![Release](https://github.com/denisw160/HelloWorldVaadin/actions/workflows/release.yml/badge.svg)](https://github.com/denisw160/HelloWorldVaadin/actions/workflows/release.yml)
[![Docker Hub](https://img.shields.io/docker/v/denisw160/hello-world-vaadin?label=Docker%20Hub&sort=semver)](https://hub.docker.com/r/denisw160/hello-world-vaadin)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A minimal [Vaadin Flow](https://vaadin.com/flow) application that shows a "Hello World" page with a light/dark
color scheme selector. It's meant as a demo
and template for building, testing and releasing a Vaadin application with GitHub Actions. There's no authentication
and no database.

The same code base produces three artifacts:

| Artifact          | Use case                                    | How to get it                                        |
|-------------------|---------------------------------------------|------------------------------------------------------|
| Executable JAR    | Standalone, `java -jar`                     | `./mvnw package`, or GitHub Releases                 |
| WAR               | Deployment to an external Tomcat 11+        | `./mvnw package -Pwar`, or GitHub Releases           |
| Docker image      | Containers (amd64 and arm64)                | `docker build .`, or Docker Hub                      |

## Tech stack

- Java 25
- Vaadin 25.3 (Flow, Aura theme)
- Spring Boot 4.1 (with Actuator for health checks)
- Maven (via the included Maven Wrapper)

## Quick start

Requirements: JDK 25. Node.js isn't needed, because Vaadin downloads it automatically.

```bash
./mvnw
```

On Windows, use `mvnw.cmd`. The default goal is `spring-boot:run`. It starts the application in development mode at
http://localhost:8080.

For hot reloading of Java changes, run the application from your IDE with the Vaadin plugin
(IntelliJ IDEA, VS Code or Eclipse) using **Debug using Hotswap Agent**.

## Project structure

```
src/main/java/me/wirries/sample/
├── Application.java          Spring Boot entry point (also the WAR servlet initializer)
├── AppShell.java             Page shell: theme (Aura), default color scheme and global stylesheets
└── hello/HelloWorldView.java The single view, mapped to "/"
src/main/resources/
├── application.properties    Configuration (port, Vaadin, Actuator)
├── banner.txt                Startup banner with app, Vaadin, Spring Boot and Java versions
└── META-INF/resources/styles.css
src/test/java/...             Browserless UI tests and a health endpoint test
.github/workflows/            CI and release pipelines
```

## Build

Every build creates the optimized production frontend bundle. No separate production profile is needed.

### Executable JAR (default)

```bash
./mvnw clean package
java -jar target/hello-world-vaadin-0.1.0-SNAPSHOT.jar
```

### WAR for Tomcat

```bash
./mvnw clean package -Pwar
```

Copy `target/hello-world-vaadin-<version>.war` into Tomcat's `webapps/` directory. Rename it to `ROOT.war` to serve it
at the root context path.

> [!IMPORTANT]
> Spring Boot 4 and Vaadin 25 require **Jakarta EE 11 (Servlet 6.1)**, which means **Apache Tomcat 11 or later**.
> Tomcat 10.x and older won't work.

The WAR is also executable with `java -jar`, so you can use it as a fallback.

### Docker image

```bash
docker build -t hello-world-vaadin .
docker run --rm -p 8080:8080 -m 1g hello-world-vaadin
```

The image builds the application from source in a multi-stage build, so you don't need a local JDK. The runtime image:

- uses `eclipse-temurin:25-jre-alpine` and runs as a non-root user,
- stores dependencies and application code in separate layers for faster rebuilds,
- has a `HEALTHCHECK` against `/actuator/health`,
- uses up to 75% of the container memory limit (`-m`) for the Java heap.

To build a specific version, pass `--build-arg REVISION=1.2.3`.

### Version number

The version is defined once in `pom.xml` as `<revision>` (CI-friendly versioning). Local builds use
`0.1.0-SNAPSHOT`. The release workflow overrides it with the version from the Git tag (`-Drevision=1.2.3`), so you
never commit a version bump.

## Health check

| Endpoint                                | Purpose                             |
|-----------------------------------------|-------------------------------------|
| `/actuator/health`                      | Overall status (`{"status":"UP"}`)  |
| `/actuator/health/liveness`             | Liveness probe (e.g. Kubernetes)    |
| `/actuator/health/readiness`            | Readiness probe (e.g. Kubernetes)   |

Only the health endpoint is exposed. No other Actuator endpoints are reachable over HTTP.

## Tests

```bash
./mvnw verify
```

- `HelloWorldViewTest` is a [browserless UI test](https://vaadin.com/docs/latest/building-apps/testing/browserless).
  It drives the real view on the server side, without a browser: greeting and color scheme selector.
- `ActuatorHealthTest` starts the application on a random port and checks the health endpoint.

## Continuous integration and releases

### CI (`.github/workflows/ci.yml`)

Runs on every push to `main`, on pull requests and on manual dispatch:

1. Builds the JAR and runs all tests.
2. Builds the WAR.
3. Builds the Docker image and smoke-tests the container health endpoint (nothing is pushed).

The JAR and WAR are available as workflow artifacts for 7 days.

### Release (`.github/workflows/release.yml`)

Pushing a version tag creates a release:

```bash
git tag v0.1.0
git push origin v0.1.0
```

The workflow then:

1. builds and tests the JAR and WAR with the tag version,
2. creates a **GitHub Release** with the JAR, WAR and a `SHA256SUMS` file attached, plus generated release notes,
3. builds a multi-arch Docker image (`linux/amd64`, `linux/arm64`) with SBOM and provenance attestations and pushes
   it to **Docker Hub**.

Versioning rules:

| Tag              | GitHub Release | Docker tags                   |
|------------------|----------------|-------------------------------|
| `v0.1.0`         | Pre-release    | `0.1.0`, `0.1`, `latest`      |
| `v1.2.3`         | Release        | `1.2.3`, `1.2`, `latest`      |
| `v1.3.0-rc.1`    | Pre-release    | `1.3.0-rc.1`                  |

All `0.x` versions and versions with a suffix are marked as pre-releases on GitHub.

> [!TIP]
> If a release fails, fix the cause, delete the tag (`git push --delete origin v1.2.3` and `git tag -d v1.2.3`) and
> the GitHub Release if one was created, then tag again.

### Docker Hub overview (`.github/workflows/dockerhub-description.yml`)

The Docker Hub repository overview is maintained in [DOCKERHUB.md](DOCKERHUB.md). Whenever the file changes on `main`
(or on manual dispatch), the workflow publishes it to Docker Hub together with the short description. It needs the
secret `DOCKERHUB_DESCRIPTION_TOKEN`, a Docker Hub access token with **Read, Write, Delete** scope.

### Dependency updates

[Dependabot](.github/dependabot.yml) checks weekly for updates to Maven dependencies, GitHub Actions and the Docker
base images, and opens pull requests for them.

## Development with AI agents

[AGENTS.md](AGENTS.md) holds the conventions for AI coding agents. The project includes an [`.mcp.json`](.mcp.json)
that connects agents to the [Vaadin MCP server](https://vaadin.com/docs/latest/building-apps/mcp) for up-to-date
Vaadin documentation and API information.

Open items and ideas are tracked in [TODO.md](TODO.md).

## Learn more

- [Vaadin documentation](https://vaadin.com/docs)
- [Vaadin components](https://vaadin.com/docs/latest/components)
- [Spring Boot reference](https://docs.spring.io/spring-boot/)

## License

[MIT](LICENSE)
