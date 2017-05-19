FROM    psteiner/base

LABEL maintainer "psteiner <psteiner@redhat.com>"

ENV FUSE_INSTALL_IMAGE=jboss-fuse-karaf-6.3.0.redhat-187.zip \
    FUSE_INSTALL_DIR=jboss-fuse-6.3.0.redhat-187 \
    FUSE_LOCATION=fuse

USER root

WORKDIR $HOME

# copy fuse distribution
ADD Docker_Files/software/$FUSE_INSTALL_IMAGE $HOME/tmp/

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
    chmod +x $HOME/run.sh && \
    unzip $HOME/tmp/$FUSE_INSTALL_IMAGE && \
    mv $HOME/$FUSE_INSTALL_DIR $HOME/$FUSE_LOCATION

# remove some overhead
RUN rm -rf $HOME/tmp/$FUSE_INSTALL_IMAGE && \
    rm -rf $HOME/$FUSE_LOCATION/extras && \
    rm -rf $HOME/$FUSE_LOCATION/quickstarts


# remove dummy projects again
RUN rm -rf $HOME/tmp/src && \
    rm -rf $HOME/tmp/pom.xml

# move configuration(s) to their right place
RUN mv $HOME/tmp/activemq.xml $HOME/$FUSE_LOCATION/etc

# creates user
RUN sed -i "s/#admin=admin/admin=change12_me/" $HOME/$FUSE_LOCATION/etc/users.properties && \
    sed -i "s/#activemq.jmx.user/activemq.jmx.user/" $HOME/$FUSE_LOCATION/etc/system.properties && \
    sed -i "s/#activemq.jmx.password/activemq.jmx.password/" $HOME/$FUSE_LOCATION/etc/system.properties

# open ports
EXPOSE 22 8101 8181 44444 1099 61616 80 8080

# ENTRYPOINT $HOME/$FUSE_LOCATION/bin/fuse
CMD $HOME/run.sh

# Build command -> docker build --rm -t psteiner/fuse .
# run command -> docker run -d -p 10000:8181 psteiner/fuse
# run and connect -> docker run -p 10000:8181 -i -t psteiner/fuse /bin/bash
