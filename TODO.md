# TODO

Open items and ideas for later. Move an item to "Done" (or delete it) once it's implemented.

## Open

_Nothing open at the moment._

## Done

- [x] Light/dark color scheme selector (System, Light, Dark), remembered in the session.
- [x] Docker Hub overview (`DOCKERHUB.md`), synced automatically by a workflow.
- [x] Hello World view, browserless UI test, health endpoint.
- [x] JAR (default), WAR (`-Pwar`) and Docker image builds.
- [x] CI and tag-based release workflows, Dependabot.
- [x] Create the Docker Hub secrets.

## Dropped

- Publishing the image to the GitHub Container Registry (`ghcr.io`): Docker Hub is enough.
- GraalVM native image: not relevant for this demo.
- Automated WAR test against Tomcat 11 in CI: the WAR release was tested manually in Tomcat.
