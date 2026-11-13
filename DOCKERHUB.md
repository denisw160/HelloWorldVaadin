# Hello World Vaadin

A minimal [Vaadin Flow](https://vaadin.com/flow) web application that greets you by name. It's a demo of building,
testing and releasing a Vaadin application with GitHub Actions. There's no authentication and no database.

- **Source code:** [github.com/denisw160/HelloWorldVaadin](https://github.com/denisw160/HelloWorldVaadin)
- **Releases (JAR and WAR):** [GitHub Releases](https://github.com/denisw160/HelloWorldVaadin/releases)
- **Issues:** [GitHub Issues](https://github.com/denisw160/HelloWorldVaadin/issues)

## Quick start

```bash
docker run --rm -p 8080:8080 denisw160/hello-world-vaadin
```

Then open [http://localhost:8080](http://localhost:8080).

## Tags

| Tag       | Description                                                     |
|-----------|-----------------------------------------------------------------|
| `latest`  | The most recent release                                         |
| `X.Y.Z`   | A specific release, e.g. `0.1.0` (recommended for deployments)  |
| `X.Y`     | The newest patch release of a minor version, e.g. `0.1`         |

Release candidates (e.g. `1.0.0-rc.1`) only get their full version tag, never `latest`.

All tags are multi-arch images for **`linux/amd64`** and **`linux/arm64`**.

## What's inside

- Java 25 (Eclipse Temurin JRE on Alpine Linux)
- Vaadin 25 (Flow, Aura theme) in production mode
- Spring Boot 4 with Actuator health checks
- Runs as an unprivileged, non-root user (`app`)
- SBOM and build provenance attestations

## Configuration

| Setting               | Default | Description                                                          |
|-----------------------|---------|----------------------------------------------------------------------|
| Port                  | `8080`  | HTTP port inside the container                                       |
| `PORT`                | `8080`  | Environment variable to change the HTTP port                         |
| `JAVA_TOOL_OPTIONS`   | –       | Additional JVM options, e.g. `-Duser.timezone=Europe/Berlin`         |
| `SPRING_*`, `SERVER_*`| –       | Any Spring Boot property as an environment variable, e.g. `LOGGING_LEVEL_ROOT=DEBUG` |

### Memory

The Java heap uses up to **75% of the container memory limit**. Vaadin keeps the UI state of every user session in
the heap, so set a memory limit that fits your load:

```bash
docker run -d -p 8080:8080 -m 1g denisw160/hello-world-vaadin
```

## Health check

The image has a built-in `HEALTHCHECK`, so `docker ps` shows the container as `healthy` once the application is
ready. The endpoints are also available for load balancers and orchestrators:

| Endpoint                     | Purpose                                 |
|------------------------------|-----------------------------------------|
| `/actuator/health`           | Overall status (`{"status":"UP"}`)      |
| `/actuator/health/liveness`  | Liveness probe (e.g. Kubernetes)        |
| `/actuator/health/readiness` | Readiness probe (e.g. Kubernetes)       |

## Docker Compose

```yaml
services:
  hello-world-vaadin:
    image: denisw160/hello-world-vaadin:latest
    ports:
      - "8080:8080"
    environment:
      - JAVA_TOOL_OPTIONS=-Duser.timezone=Europe/Berlin
    mem_limit: 1g
    restart: unless-stopped
```

## Running behind a reverse proxy

Vaadin keeps the UI state on the server, in the user's HTTP session. If you run more than one container behind a
load balancer or reverse proxy (e.g. nginx or Traefik), enable **sticky sessions**.

## License

[MIT](https://github.com/denisw160/HelloWorldVaadin/blob/main/LICENSE)
