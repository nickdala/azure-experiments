ARG VARIANT="jammy"
FROM mcr.microsoft.com/vscode/devcontainers/base:0-${VARIANT}

COPY scripts/dapr-install.sh /tmp/scripts/
RUN bash /tmp/scripts/dapr-install.sh

RUN export DEBIAN_FRONTEND=noninteractive \
    && apt-get update \
    && apt-get -y install --no-install-recommends jq
