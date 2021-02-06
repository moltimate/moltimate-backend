#!/bin/bash
if [ "$1" == "prod" ]; then
  cp "prod_config.yml" "prod_config.config";
else
  cp "dev_config.yml" "dev_config.config";
fi
