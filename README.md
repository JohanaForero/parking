# Parking

## requirements:

java 21
docker

## docker compose

Before compiling and before starting the project,
in the terminal, locate the root of this project, enter the docker directory, and run the command `docker compose up`

## compile

in the terminal, locate the root of this project, and run the command `./gradlew clean build`

## run

in the terminal, locate the root of this project, and run the command `./gradlew bootRun`

### swagger

swagger at http://localhost:9081/swagger-ui/index.html

### postman

Collection postman at the root of this project, enter the postman directory, file with the name
`Parking.postman_collection.json`

### get token

get token with:

```
curl --location 'http://localhost:8080/realms/parking/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'client_id=parking-client' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'username=admin@mail.com' \
--data-urlencode 'password=admin'
```

### create user

to create users:
http://localhost:8080/admin/master/console/#/parking/users
user: admin
password: test
