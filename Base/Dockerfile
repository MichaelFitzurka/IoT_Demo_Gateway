FROM    resin/rpi-raspbian:jessie

LABEL maintainer "psteiner <psteiner@redhat.com>"

# Update the system and Install necessary RPMs
RUN apt-get update && \
    apt-get install apt-utils unzip && \
    apt-get upgrade && \
    apt-get autoremove && \
    apt-get autoclean

# Make sure required environments are set
ENV JAVA_HOME=/opt/jdk1.8.0_131/ \
    HOME=/home/psteiner \
    MAVEN_HOME=/opt/apache-maven-3.5.0

# Set root password
RUN echo "root:redhat" | /usr/sbin/chpasswd

# copy java distribution
ADD Docker_Files/software/jdk-8u131-linux-arm32-vfp-hflt.tar.gz /opt/

RUN update-alternatives --install /usr/bin/java java /opt/jdk1.8.0_131/bin/java 1 && \
    update-alternatives --install /usr/bin/javac javac /opt/jdk1.8.0_131/bin/javac 1 && \
    update-alternatives --set java /opt/jdk1.8.0_131/bin/java && \
    update-alternatives --set javac /opt/jdk1.8.0_131/bin/javac

# copy maven distribution
ADD Docker_Files/software/apache-maven-3.5.0-bin.tar.gz /opt/

# download and install maven
RUN echo export M2_HOME=/opt/apache-maven-3.5.0 > /etc/profile.d/maven.sh && \
    sed -i '$a export PATH=$PATH:$M2_HOME/bin' /etc/profile.d/maven.sh

# Create user to run processed
RUN useradd -m -d /home/psteiner -p psteiner psteiner

# Switch to new user
USER psteiner

# create a few needed directories
RUN mkdir $HOME/tmp && \
    mkdir $HOME/.m2

# transfer maven configuration file
ADD config/settings.xml $HOME/.m2/

# as ADD always uses root as user, we have to give
# the file back to 'psteiner'
USER root
RUN chown -R psteiner $HOME/.m2/*
USER psteiner

# lets create a volume to mount the external maven repo
VOLUME /maven_repo

# make $HOME/tmp the active directory
WORKDIR $HOME/tmp

# Build command -> docker build --rm -t psteiner/base .
# run command -> docker run -d psteiner/base
# run command ->  docker run -i -t -v /home/psteiner/.m2/repository/:/maven_repo  psteiner/base /bin/bash
