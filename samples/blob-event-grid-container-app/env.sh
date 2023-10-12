export SUBSCRIPTION_ID=CHANGE-ME      # replace it with your subscription-id
export REGION="eastus"                # Replace with your desired region

export PROJECT="blob-subsription-sample-nickdala"
export RESOURCE_GROUP="rg-$PROJECT"
export TAG="blob-subsription-sample"

# Storage
export STORAGE_ACCOUNT_NAME="stblobnickdala"   # The name must be unique across all existing storage account names in Azure. It must be 3 to 24 characters long, and can contain only lowercase letters and numbers
export STORAGE_CONTAINER_NAME_TEXT="text"
export QUEUE_NAME="text-queue"
export EVENT_SUBSCRIPTION_NAME="evgs-$PROJECT-text"

# ACA
export CONTAINER_REGISTRY_NAME="crnickdala"  # alphanumerics between 5 and 50 characters
export CONTAINER_IMAGE_NAME="blob-container-app-nickdala:1.0"
export ACA_ENVIRONMENT="nickdala"
export ACA_JOB_NAME="print-blob-event-job"

echo "Environment Variables"
echo "---------------------"
echo "SUBSCRIPTION_ID=$SUBSCRIPTION_ID"
echo "PROJECT=$PROJECT"
echo "REGION=$REGION"
echo "RESOURCE_GROUP=$RESOURCE_GROUP"
echo "TAG=$TAG"
echo "STORAGE_ACCOUNT_NAME=$STORAGE_ACCOUNT_NAME"
echo "STORAGE_CONTAINER_NAME_TEXT=$STORAGE_CONTAINER_NAME_TEXT"
echo "QUEUE_NAME=$STORAGE_QUEUE_TEXT"
echo "EVENT_SUBSCRIPTION_NAME=$EVENT_SUBSCRIPTION_NAME"
echo "CONTAINER_REGISTRY_NAME=$CONTAINER_REGISTRY_NAME"
echo "CONTAINER_IMAGE_NAME=$CONTAINER_IMAGE_NAME"
echo "ACA_JOB_NAME=$ACA_JOB_NAME"
echo "---------------------"

