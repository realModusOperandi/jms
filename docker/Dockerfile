FROM ibmcom/websphere-traditional:9.0.5.0-ubi

COPY --chown=was:root jms.war /work/app/
COPY --chown=was:root configure.py /work/config/
RUN /work/configure.sh