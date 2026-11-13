# TODO

Open items and ideas for later. Move an item to "Done" (or delete it) once it's implemented.

## Open

- [ ] Also publish the image to the GitHub Container Registry (`ghcr.io`).
- [ ] Test the WAR deployment against a real Tomcat 11 in CI (e.g. with the `tomcat:11-jre25` image).
- [ ] Add a light/dark mode toggle (`@ColorScheme`) as a small UI showcase.
- [ ] Consider a GraalVM native image as a fourth artifact.

## Done

- [x] Docker Hub overview (`DOCKERHUB.md`), synced automatically by a workflow.
- [x] Hello World view, browserless UI test, health endpoint.
- [x] JAR (default), WAR (`-Pwar`) and Docker image builds.
- [x] CI and tag-based release workflows, Dependabot.
- [x] Create the Docker Hub secrets.
