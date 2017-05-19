FROM    psteiner/fuse

LABEL maintainer "psteiner <psteiner@redhat.com>"

USER root

ENV BUNDLE_NAME=smart_gateway-1.0.0.jar \
    BROKER_NAME=smartgateway

# copy required ( or not ) configurations
ADD Docker_Files/config/activemq.xml $HOME/tmp/

# copy helper_install script
ADD Docker_Files/scripts/install.sh $HOME/tmp/
ADD Docker_Files/scripts/run.sh $HOME/

# let the user 'psteiner' own the files
RUN chown -R psteiner $HOME

# continue as 'psteiner'
USER psteiner

RUN chmod +x $HOME/tmp/install.sh && \
    chmod +x $HOME/run.sh

# run the helper_install script
WORKDIR $HOME/tmp
RUN $HOME/tmp/install.sh

# move configuration(s) to their right place
RUN mv $HOME/tmp/activemq.xml $HOME/$FUSE_LOCATION/etc

# open ports
EXPOSE 22 8101 8181 44444 1099 61616 80 8080 4711

# ENTRYPOINT $HOME/$FUSE_LOCATION/bin/fuse
CMD $HOME/run.sh

# Build command -> docker build --rm -t workspacefuse_iotdatacenterreceiver .
# run command -> docker run -d -p 10000:8181 workspacefuse_iotdatacenterreceiver
# run and connect -> docker run -p 10000:8181 -i -t workspacefuse_iotdatacenterreceiver /bin/bash
