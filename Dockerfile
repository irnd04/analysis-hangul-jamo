FROM gradle:8.14-jdk21 AS builder

WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle bundlePlugin --no-daemon

FROM docker.elastic.co/elasticsearch/elasticsearch:9.3.0

COPY --from=builder /app/build/distributions/analysis-hangul-jamo-*.zip /tmp/plugin.zip
RUN elasticsearch-plugin install --batch file:///tmp/plugin.zip
