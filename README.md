# Reveal.world sitemap generator 

## Motivation

There is two application versions:
1) Old, written on CRA (frontend, placed on S3) + Play Framework 2.7 (backend, placed on EC2-Instance)
2) New, written on Next.js (frontend) + Play Framework 3 (backend), placed on Docker in one EC2-Instance. The aim of app is to replace old one.

The problem is that both should return sitemap.xml. For the first problem was resolved by generating sitemap.xml on the backend and saving it to EC2 place, on Play was added route to return sitemap.
This is not the best solution because sitemap lies on different domain.
For the second sitemap generates on the fly by on frontend, using backend data. So every request to sitemap lead to additional loading

This service should decide problems of both versions. It should work on AWS Lambda and runs by EventBridge event (another option run by Kafka event). Service generates sitemap.xml file and saves it to AWS S3 (for the old app version it is public folder, for the new version this is standalone S3 bucket)

Because we have two applications it is truly should be two services, but by some reasons:
- both apps work on one database, 
- to exclude code duplication, 
- there is only one developer on project
was decided to create one service that should be decided (or simplified if old app will be closed) in the future.
Kafka Consumer was added to demonstrate ability to work with Kafka. Really this should be standalone service too.

## Environment variables

- REVEAL_MODE determines urls in sitemap. Values: _cra_ for old application, _next_ for new application. Default is _next_.
- REVEAL_PATH is path on S3 or on local filesystem where sitemap xml will be placed. If not defined error will be occurred.
- REVEAL_HOST is base host that should be included in urls. Default is _https://reveal.world_
- REVEAL_LOCAL if set to true sitemap saves to local filesystem. Default _false_.
- REVEAL_KAFKA if _true_ service runs as Kafka consumer. Default _false_.

- REVEAL_POSTGRES_URL is PostgresSQL database connection url
- REVEAL_POSTGRES_USERNAME is PostgreSQL connection username
- REVEAL_POSTGRES_PASSWORD is PostgeSQL connection password

AWS settings is necessary when sitemap.xml uploads to AWS S3 bucket (REVEAL_LOCAL is _false_):
- REVEAL_AWS_KEY is AWS connection key
- REVEAL_AWS_SECRET is AWS connection secret key
- REVEAL_AWS_REGION is AWS connection region
- REVEAL_AWS_BUCKET is S3 bucket name to upload file

## Cassandra 

Cassandra database added in demo purposes so tables limited and simplified.
To run application using Cassandra it is necessary to:
1) run Cassandra Docker using docker-compose.yml from project root
2) create tables using cassandra.sql:  
> docker exec -it container-name cqlsh -f /tmp/cassandra.cql

## Notes
Loading data from database performs sequential because sitemap should be updated not often (several times per day is maximum) so it is not necessary to load database by parallel requests.
