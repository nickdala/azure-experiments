# OpenAI Sample

This sample demonstrates how to use the OpenAI API to generate text.

## Setup

### 1. Clone the repository and change directory

```bash
git clone https://github.com/nickdala/azure-experiments.git

cd azure-experiments/samples/openai
```

### 2. Login to Azure

Login to Azure using the Azure CLI.

```shell
az login
```

Optional: Set the default subscription. If you have multiple subscriptions, you can list them using `az account list`.

```shell
az account list --output table

az account set --subscription <subscription-id>
```

### 3. Set environment variables

Source the `env.sh` file in the *scripts* directory. You can edit the `env.sh` file to change the default values. Note: Some Azure resources must have globally unique names.

**At a minimum, you must change the `COSMOSDB_PASSWORD`.**

```bash
source ./scripts/env.sh
```

### 3. Deploy AKS

1. Create the resource group

    ```
    az group create \
      --name $RESOURCE_GROUP \
      --location "$REGION" \
      --tags system="$TAG"
    ```

1. Create an AKS cluster.

    ```bash
    az aks create \
    --resource-group $RESOURCE_GROUP \
    --name $AKS_CLUSTER_NAME \
    --node-count 2 \
    --enable-addons http_application_routing \
    --generate-ssh-keys
    ```

1. Get the AKS credentials

    ```bash
    az aks get-credentials -n $AKS_CLUSTER_NAME -g $RESOURCE_GROUP
    ```

### 4. Deploy Azure Container Registry

1. Create the Azure Container Registry

    ```bash
    az acr create \
    --resource-group $RESOURCE_GROUP \
    --name $CONTAINER_REGISTRY_NAME \
    --sku Basic \
    --admin-enabled true \
    --location $REGION \
    --tags system="$TAG"
    ```
1. Log into the Azure Container Registry

    ```bash
    az acr login --name $CONTAINER_REGISTRY_NAME
    ```

