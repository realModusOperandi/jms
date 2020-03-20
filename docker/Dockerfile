FROM adoptopenjdk/openjdk13-openj9:ubi-minimal as builder

# Assumes build context is entire repo
WORKDIR /work
ADD . /work/

RUN ./gradlew war

FROM openliberty/open-liberty:full-java13-openj9-ubi

RUN mkdir /opt/ol/wlp/usr/shared/resources/wmq/ && chown -R 1001:0 /opt/ol/wlp/usr/shared/resources/wmq/
COPY --chown=1001:0 docker/wmq.jmsra.rar /opt/ol/wlp/usr/shared/resources/wmq/

COPY --chown=1001:0 jms.ear_server.xml /config/server.xml
COPY --chown=1001:0 --from=builder /work/build/libs/jms.war /config/apps/

ARG VERBOSE=true
RUN configure.sh