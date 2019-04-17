FROM hortonworks/hwx_openjdk:11.0-jdk-slim
MAINTAINER info@hortonworks.com

# REPO URL to download jar
#ARG REPO_URL=https://repo.hortonworks.com/content/repositories/releases
#ARG VERSION=''

ENV VERSION ${VERSION}

WORKDIR /

# Install zip
RUN apt-get update --no-install-recommends && apt-get install -y zip procps && apt-get clean && rm -rf /var/lib/apt/lists/*

# Install zip
RUN apt-get update --no-install-recommends && apt-get install -y zip procps

# install the rdb app
#ADD ${REPO_URL}/com/sequenceiq/rdb/$VERSION/rdb-$VERSION.jar /rdb.jar
# Artifact isn't published yet, so copy it into the build directory before running this
ADD rdb.jar /rdb.jar

# extract schema files
RUN ( unzip rdb.jar schema/* -d / ) || \
    ( unzip rdb.jar BOOT-INF/classes/schema/* -d /tmp/ && mv /tmp/BOOT-INF/classes/schema/ /schema/ )

# Install starter script for the RDB application
COPY bootstrap/start_rdb_app.sh /
COPY bootstrap/wait_for_rdb_api.sh /

ENTRYPOINT ["/start_rdb_app.sh"]
