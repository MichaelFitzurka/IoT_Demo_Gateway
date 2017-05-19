FROM    psteiner/base

LABEL maintainer "psteiner <psteiner@redhat.com>"

USER root

ENV APPL=rules_cep-jar-with-dependencies.jar

# transfer run-script
ADD Docker_Files/scripts/run.sh $HOME/

# make 'psteiner' the owner
RUN chown -R psteiner $HOME/run.sh 

# switch to psteiner
USER psteiner

WORKDIR $HOME/

CMD $HOME/run.sh

# Build command -> docker build --rm -t workspace_iotdatacentermonitor .
# run command -> docker run -h iotcontrollerrouter -d workspacefuse_iotdatacentermonitor
# run and connect -> docker run -P -i -t -h iotdatacentermonitor workspacefuse_iotdatacentermonitor /bin/bash
