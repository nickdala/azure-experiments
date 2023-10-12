package main

import (
	"context"
	"encoding/base64"
	"encoding/json"
	"fmt"
	"log"
	"os"

	"github.com/Azure/azure-sdk-for-go/sdk/azcore/to"
	"github.com/Azure/azure-sdk-for-go/sdk/storage/azqueue"
	"github.com/joho/godotenv"
)

// https://learn.microsoft.com/en-us/azure/event-grid/event-schema-blob-storage?toc=%2Fazure%2Fstorage%2Fblobs%2Ftoc.json&tabs=event-grid-event-schema#microsoftstorageblobcreated-event
type BlobCreatedEvent struct {
	Id        string `json:"id"`
	Topic     string `json:"topic"`
	EventType string `json:"eventType"`
	Data      struct {
		Api string `json:"api"`
		URL string `json:"url"`
	}
}

func main() {
	godotenv.Load()

	log.Printf("Executing %v!", os.Args[0])

	// 1. Connect to the queue
	connectionString := os.Getenv("AZURE_STORAGE_CONNECTION_STRING")
	queueServiceClient, err := azqueue.NewServiceClientFromConnectionString(connectionString, nil)
	if err != nil {
		log.Fatalf("Error creating queue client: %v", err)
	}

	queueName := os.Getenv("AZURE_STORAGE_QUEUE_NAME")

	// create queue client
	queueClient := queueServiceClient.NewQueueClient(queueName)

	// 2. Dequeue message
	// The message isn't automatically deleted from the queue, but after it has been retrieved, it isn't visible to other clients for the time interval that's specified by the visibilitytimeout parameter in seconds.
	resp, err := queueClient.DequeueMessage(context.TODO(), &azqueue.DequeueMessageOptions{VisibilityTimeout: to.Ptr(int32(30))})
	if err != nil {
		log.Fatalf("Error dequeueing message: %v", err)
	}

	if len(resp.Messages) == 0 {
		log.Fatalf("No messages in queue %s", queueName)
	}

	message := resp.Messages[0]
	messageText := message.MessageText

	// 3. Get blob created event
	blobCreatedEvent, err := getBlobCreatedEvent(messageText)
	if err != nil {
		log.Fatalf("Error getting blob created event: %v", err)
	}

	log.Printf("This is id %s\n", blobCreatedEvent.Id)
	log.Printf("This is url %s\n", blobCreatedEvent.Data.URL)
	log.Printf("This is api %s\n", blobCreatedEvent.Data.Api)
	log.Printf("This is topic %s\n", blobCreatedEvent.Topic)
	log.Printf("This is event type %s\n", blobCreatedEvent.EventType)

	// 4. Delete message
	// The message is deleted from the queue.
	if _, err := queueClient.DeleteMessage(context.Background(), *message.MessageID, *message.PopReceipt, nil); err != nil {
		log.Fatalf("Deleting message from queue failed %v\n", message.MessageID)
	}

	log.Println("Done!")
}

func getBlobCreatedEvent(messageText *string) (*BlobCreatedEvent, error) {
	log.Println("Getting blob created event")

	str, err := base64.StdEncoding.DecodeString(*messageText)
	if err != nil {
		return nil, fmt.Errorf("error decoding string: %v", err)
	}

	log.Printf("%q\n", str)

	blobCreatedEvent := BlobCreatedEvent{}
	error := json.Unmarshal([]byte(str), &blobCreatedEvent)
	if error != nil {
		return nil, fmt.Errorf("error in JSON unmarshalling from json marshalled object: %v", error)
	}

	return &blobCreatedEvent, nil
}
