#!/usr/bin/env bash

echo "Installing Dapr"

wget -q https://raw.githubusercontent.com/dapr/cli/master/install/install.sh -O - | /bin/bash
