# AGENTS.md

Guidance for AI coding agents (Claude Code, Codex, Cursor, …) working in this repository.

## Project

Minimal public demo: a Vaadin Flow "Hello World" application with no authentication and no database. The focus is
a clean build and release setup that produces three artifacts: executable JAR, WAR (Tomcat 11+) and Docker image (Docker
Hub). Keep it small and simple; don't add features that the demo doesn't need.

## Language

- **Everything in this repository is in English**: source code, identifiers, comments, Javadoc, commit messages,
  documentation, workflow names and log messages.
- The user may talk to you in German. Answer in the user's language, but write repository content in English.

## Tech stack

- Java 25, Maven via the wrapper (`./mvnw`, on Windows `mvnw.cmd`). Never require a globally installed Maven.
- Vaadin 25.3 (Flow, server-side Java UI; **no** Hilla/React), Aura theme.
- Spring Boot 4.1 (Jakarta EE 11 / Servlet 6.1). Spring Boot 3.x APIs and `javax.*` don't apply.
- Spring Boot Actuator, only the `health` endpoint is exposed.

## Vaadin conventions

- Vaadin changes faster than model training data. **Before writing Vaadin code, use the Vaadin MCP server**
  (configured in `.mcp.json`): `get_vaadin_primer`, `search_vaadin_docs` and `get_java_symbol` for version 25.3.
  Don't guess APIs.
- Theme: Aura is loaded via `@StyleSheet(Aura.STYLESHEET)` in `AppShell`. The default color scheme is
  `@ColorScheme(LIGHT_DARK)` (follows the OS); the view switches it with `Page.setColorScheme()`. Never use the
  `dark` theme attribute. Use `--aura-*` / `--vaadin-*` CSS
  properties, never `--lumo-*`. Don't use `@Theme` or `frontend/themes`.
- Prefer theme-neutral variants (e.g. `ButtonVariant.PRIMARY`) over `LUMO_*` / `AURA_*` variants.
- Views live in feature packages under `me.wirries.sample` (e.g. `hello/HelloWorldView.java`), annotated with
  `@Route` and `@PageTitle`.
- For UI state, prefer Signals (`ValueSignal`, `bindText`, `bindValue`, …) over hand-written listener plumbing once
  the UI needs shared state.
- `vaadin-dev` stays `<optional>true</optional>`, so it never reaches production artifacts. There is no
  `production` profile in Vaadin 25.

## Build and test commands

| Task                     | Command                                   |
|--------------------------|-------------------------------------------|
| Run in development mode  | `./mvnw` (default goal `spring-boot:run`) |
| Build and test (JAR)     | `./mvnw clean verify`                     |
| Build WAR                | `./mvnw clean package -Pwar`              |
| Build Docker image       | `docker build -t hello-world-vaadin .`    |
| Build a specific version | add `-Drevision=1.2.3`                    |

- `JAVA_HOME` must point to a JDK 25. The build fails with "release version 25 not supported" otherwise.
- Run `./mvnw clean verify` before you consider a change done. All tests must pass.
- UI tests are **browserless tests** (`browserless-test-spring`, package `com.vaadin.browserless`): use
  `BrowserlessApplicationContext` → `newUser().newWindow()` and find components by label or text. The shared
  context is defined in `src/test/java/me/wirries/sample/BrowserlessContextConfiguration.java`.
- `banner.txt` and `application*.properties` are Maven-filtered: `@project.version@`-style placeholders are
  replaced at build time, `${...}` placeholders are resolved by Spring at runtime.

## Versioning and packaging

- The version is the CI-friendly property `<revision>` in `pom.xml` (default `0.1.0-SNAPSHOT`). Don't hard-code
  versions elsewhere; don't commit version bumps. Releases set the version from the Git tag.
- Packaging is controlled by the property `packaging.type` (`jar` by default, `war` in the `war` profile). The `war`
  profile marks `spring-boot-starter-tomcat` as `provided`.
- `Application` extends `SpringBootServletInitializer`, which the WAR deployment needs. Keep it.

## CI/CD

- `.github/workflows/ci.yml`: build and test the JAR, build the WAR, build and smoke-test the Docker image.
- `.github/workflows/release.yml`: triggered by tags `v*.*.*`; publishes a GitHub Release (JAR, WAR, `SHA256SUMS`)
  and the multi-arch Docker image to Docker Hub. Needs the secrets `DOCKERHUB_USERNAME` and `DOCKERHUB_TOKEN`;
  optional variable `DOCKERHUB_IMAGE`.
- `.github/workflows/dockerhub-description.yml`: publishes `DOCKERHUB.md` as the Docker Hub overview when it
  changes on `main`. Needs the secret `DOCKERHUB_DESCRIPTION_TOKEN` (scope Read, Write, Delete). Keep `DOCKERHUB.md`
  in sync with the image (tags, configuration, platforms); use absolute links only, Docker Hub can't resolve
  relative ones.
- `0.x` versions and suffixed versions (`-rc.1`) are GitHub pre-releases.
- Pin actions to major versions (`@v7`); Dependabot keeps them up to date.
- The Docker build stage runs on `$BUILDPLATFORM` (the JAR is platform independent). Keep it that way so that
  multi-arch builds don't run Maven under QEMU.

## Workflow

- This is a demo project: work directly on `main`, without feature branches or pull requests, unless asked
  otherwise.
- Keep `README.md` up to date when you change the build, the workflows or the configuration.
- Record open items and ideas for later in `TODO.md` instead of implementing them unasked.
