# syntax=docker/dockerfile:1

# ---------------------------------------------------------------------------
# Build stage: compiles the application and the production frontend bundle.
# It always runs on the build host's native platform, because the JAR is
# platform independent. This keeps multi-arch builds fast (no QEMU for Maven).
# ---------------------------------------------------------------------------
FROM --platform=$BUILDPLATFORM eclipse-temurin:25-jdk AS build

# Application version, set by the release workflow (e.g. 1.2.3)
ARG REVISION=0.1.0-SNAPSHOT

WORKDIR /build

# Resolve Maven dependencies first, so this layer is cached until pom.xml changes
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -ntp dependency:go-offline -Drevision=${REVISION}

COPY src/ src/
RUN --mount=type=cache,target=/root/.m2 \
    --mount=type=cache,target=/root/.vaadin \
    ./mvnw -B -ntp package -DskipTests -Drevision=${REVISION} \
 && cp target/hello-world-vaadin-*.jar application.jar \
 && java -Djarmode=tools -jar application.jar extract --layers --launcher --destination extracted

# ---------------------------------------------------------------------------
# Runtime stage: small JRE image, non-root user, layered application.
# ---------------------------------------------------------------------------
FROM eclipse-temurin:25-jre-alpine

ARG REVISION=0.1.0-SNAPSHOT
LABEL org.opencontainers.image.title="hello-world-vaadin" \
      org.opencontainers.image.description="Minimal Vaadin Flow Hello World application" \
      org.opencontainers.image.source="https://github.com/denisw160/HelloWorldVaadin" \
      org.opencontainers.image.licenses="MIT" \
      org.opencontainers.image.version="${REVISION}"

RUN addgroup -S app && adduser -S app -G app
WORKDIR /application

# Copy the layers from least to most frequently changing for better caching
COPY --from=build /build/extracted/dependencies/ ./
COPY --from=build /build/extracted/spring-boot-loader/ ./
COPY --from=build /build/extracted/snapshot-dependencies/ ./
COPY --from=build /build/extracted/application/ ./

USER app
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD wget -q -O /dev/null http://localhost:8080/actuator/health || exit 1

# Let the heap use 75% of the container's memory limit instead of the default 25%
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "org.springframework.boot.loader.launch.JarLauncher"]
