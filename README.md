# RSO: Customers microservice

## Prerequisites

```bash
docker run -d --name rso-customers -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=customer -p 5432:5432 postgres:latest
```

## Run application in Docker

```bash
docker run -p 8080:8080 -e KUMULUZEE_CONFIG_ETCD_HOSTS=http://192.168.99.100:2379 jmezna/rso-customers
```