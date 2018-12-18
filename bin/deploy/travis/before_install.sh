#!/usr/bin/env bash

if [ ! -d $HOME/google-cloud-sdk/bin ]; then
    rm -rf $HOME/google-cloud-sdk;
    curl https://sdk.cloud.google.com | bash > /dev/null;
fi

# Make cached/newly downloaded SDK take precedence over old SDK on machine
source $HOME/google-cloud-sdk/path.bash.inc
gcloud components install app-engine-java
gcloud version

# Decrypt credentials file
openssl aes-256-cbc \
    -K ${encrypted_a6a69c5b1756_key} \
    -iv ${encrypted_a6a69c5b1756_iv} \
    -in ${GCP_CREDENTIALS_FILE}.enc \
    -out ${GCP_CREDENTIALS_FILE} \
    -d

gcloud auth activate-service-account --key-file ${GCP_CREDENTIALS_FILE}
