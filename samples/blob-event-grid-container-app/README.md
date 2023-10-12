# Azure Blob storage to Azure Event Grid and Azure Container App Job

This pattern demonstrates how to use *Azure Blob storage* with *Azure Event Grid* subscriptions and *Azure Container Apps*. Azure Event Grid is an eventing service for the cloud. We'll demonstrate how an event is published to Azure Storage Queue once we upload a file to Azure Blob Storage. That event will be pulled and processed by an Azure Container App Job.

See the [Blob storage events schema](https://learn.microsoft.com/en-us/azure/event-grid/event-schema-blob-storage?toc=%2Fazure%2Fstorage%2Fblobs%2Ftoc.json&tabs=event-grid-event-schema) article to view the full list of the events that Blob storage supports.

## Prerequisites

- [Azure CLI](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli?view=azure-cli-latest) version 2.53.0 or later.
- [Go](https://golang.org/doc/install) version 1.21.1 or later.

There's a dev container in the repository that you can use to develop the application in a container. If you're using VS Code, you can open the repository in a container by selecting the **Remote Containers: Open Repository in Container...** command from the Command Palette (F1) or by using the **Open Repository in Container** button on the Source Control tab.

## Setup

1. Change to the directory that contains the sample code.

    ```bash
    cd examples/blob-event-grid-container-app
    ```

1. Edit and source the `env.sh` file to set the environment variables.

    ```bash
    source ./env.sh
    ```

1. Login to Azure.

    ```bash
    az login
    ```

1. Set the default subscription.

    ```bash
    az account set --subscription "$SUBSCRIPTION_ID"
    ```

### Verify that the Microsoft.EventGrid is registered.

> You must have the Event Grid provider registered in your subscription.

```bash
az provider list --query "[?contains(namespace,'Microsoft.EventGrid')]" -o table
```

If it is not registered, register it.

```bash
az provider register --namespace Microsoft.EventGrid
```

### Create Azure resources

1. Create a resource group.

    ```bash
    az group create \
      --name "$RESOURCE_GROUP" \
      --location "$REGION" \
      --tags system="$TAG"
    ```

1. Create a storage account.

    ```bash
    az storage account create \
      --name "$STORAGE_ACCOUNT_NAME" \
      --resource-group "$RESOURCE_GROUP" \
      --location "$REGION" \
      --tags system="$TAG"
    ```

    Before we can create the storage container and the storage queue, we need the storage account connection string.

1. Get the storage account connection string.

    ```bash
    STORAGE_CONNECTION_STRING=`az storage account show-connection-string --resource-group $RESOURCE_GROUP --name $STORAGE_ACCOUNT_NAME --query connectionString --output tsv`
    ```

1. Create the container.

    ```bash
    az storage container create --name $STORAGE_CONTAINER_NAME_TEXT --account-name $STORAGE_ACCOUNT_NAME --connection-string $STORAGE_CONNECTION_STRING
    ```

1. Create the queue.

    ```bash
    az storage queue create --name $QUEUE_NAME --connection-string $STORAGE_CONNECTION_STRING
    ```

1. Create the event subscription.


    First we need the storage account id and the queue endpoint.


    ```bash
    STORAGE_ACCOUNT_ID="/subscriptions/$SUBSCRIPTION_ID/resourceGroups/$RESOURCE_GROUP/providers/Microsoft.Storage/storageAccounts/$STORAGE_ACCOUNT_NAME"

    QUEUE_ENDPOINT="/subscriptions/$SUBSCRIPTION_ID/resourceGroups/$RESOURCE_GROUP/providers/Microsoft.Storage/storageAccounts/$STORAGE_ACCOUNT_NAME/queueservices/default/queues/$QUEUE_NAME"
    ```

    Now we can create the event subscription.

    ```bash
    az eventgrid event-subscription create \
    --name $EVENT_SUBSCRIPTION_NAME \
    --source-resource-id $STORAGE_ACCOUNT_ID \
    --endpoint-type storagequeue \
    --endpoint $QUEUE_ENDPOINT \
    --included-event-types Microsoft.Storage.BlobCreated \
    --subject-ends-with .txt
    ```

### Upload Sample Data to Azure Blob Storage

First we need the storage account key.

```bash
STORAGE_ACCOUNT_KEY=`az storage account keys list --resource-group $RESOURCE_GROUP --account-name $STORAGE_ACCOUNT_NAME --query "[0].value" --output tsv`
```

Verify the storage account key.

```bash
echo $STORAGE_ACCOUNT_KEY
```

Now upload the text file.

```bash
az storage blob upload \
  --account-name "$STORAGE_ACCOUNT_NAME" \
  --account-key "$STORAGE_ACCOUNT_KEY" \
  --container-name "$STORAGE_CONTAINER_NAME_TEXT" \
  --file "data/sample.txt" \
  --name "sample.txt"
```

### Take a look at the Azure Portal


### Azure Container Apps

1. Create the Azure Container Registry

    ```bash
    az acr create \
      --name "$CONTAINER_REGISTRY_NAME" \
      --resource-group "$RESOURCE_GROUP" \
      --location "$REGION" \
      --sku Basic \
      --admin-enabled true \
      --tags system="$TAG"
    ```

### Build and Deploy the Azure Container App

1. Login to the Azure Container Registry.

    ```bash
    az acr login --name "$CONTAINER_REGISTRY_NAME"
    ```

1. Build the Docker image.

    ```bash
    az acr build \
      --registry "$CONTAINER_REGISTRY_NAME" \
      --image "$CONTAINER_IMAGE_NAME" \
      --file Dockerfile \
      .
    ```

1. Verify the Docker image was pushed to the Azure Container Registry.

    ```bash
    az acr repository list --name $CONTAINER_REGISTRY_NAME --output table
    ```

1. Create the App Environment.
    
        ```bash
        az containerapp env create \
        --name "$ACA_ENVIRONMENT" \
        --resource-group "$RESOURCE_GROUP" \
        --location "$REGION" \
        --tags system="$TAG"
        ```

1. Create the Azure Container App.

    ```bash
    az containerapp job create \
    --name "$ACA_JOB_NAME" \
    --resource-group "$RESOURCE_GROUP" \
    --environment "$ACA_ENVIRONMENT" \
    --trigger-type "Event" \
    --replica-timeout "1800" \
    --replica-retry-limit "1" \
    --replica-completion-count "1" \
    --parallelism "1" \
    --min-executions "0" \
    --max-executions "10" \
    --polling-interval "60" \
    --scale-rule-name "queue" \
    --scale-rule-type "azure-queue" \
    --scale-rule-metadata "accountName=$STORAGE_ACCOUNT_NAME" "queueName=$QUEUE_NAME" "queueLength=1" \
    --scale-rule-auth "connection=connection-string-secret" \
    --image "$CONTAINER_REGISTRY_NAME.azurecr.io/$CONTAINER_IMAGE_NAME" \
    --cpu "0.25" \
    --memory "0.5Gi" \
    --secrets "connection-string-secret=$STORAGE_CONNECTION_STRING" "storage-key=$STORAGE_ACCOUNT_KEY" \
    --registry-server "$CONTAINER_REGISTRY_NAME.azurecr.io" \
    --env-vars "AZURE_STORAGE_QUEUE_NAME=$QUEUE_NAME" "AZURE_STORAGE_CONNECTION_STRING=secretref:connection-string-secret" "AZURE_STORAGE_ACCOUNT_NAME=$STORAGE_ACCOUNT_NAME" "AZURE_STORAGE_ACCOUNT_KEY=secretref:storage-key"
    ```

### Verify the event was processed by the Azure Container App

