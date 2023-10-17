FROM golang:1.21.1-alpine AS build_base

# Set the Current Working Directory inside the container
WORKDIR /src/

COPY main.go go.* /src/

# Build the Go app
RUN go build -o /out/app .

# Start fresh from a smaller image
FROM alpine:3.18

COPY --from=build_base /out/app /app
CMD ["/app"]
