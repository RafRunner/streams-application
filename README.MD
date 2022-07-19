# Streams Application

A simple kafka client-producer that receives messages from an input topic (ipstack.input by default) as a JSON string in the following format:

    {
        "client_id": <The ID of the client that emitted the event: string>,
        "time_stamp": <The time in milliseconds since the UNIX epoch: long>,
        "ip": <The IP address of the user: string>
    }

And sends them in an output topic (ipstack.output by default) with the completed information:

    {
        "client_id": <The ID of the client that emitted the event: string>,
        "time_stamp": <The time in milliseconds since the UNIX epoch: long>,
        "ip": <The IP address of the user: string>
        "latitude": <The latitude coordinate of the user: long>,
        "longitude": <The longitude coordinate of the user: long>,
        "country_name": <The country of the user. For example, United States: string>,
        "region_name": <The region of the user. For example, California: string>,
        "city": <The city of the user. For example, San Francisco: string>
    }

The geolocation information is gathered using the [IPStack](https://ipstack.com/) free API.

## How to run

- Create a .env and a docker.env (if you intend on running the main application on docker) on the project root files with the necessary configuration (the only ones with no default and needs to be present are `API_KEY` with your IPStack API key on both files and `BOOTSTRAP_SERVERS_CONFIG=//kafka:9092` on the docker.env file so the application can communicate with the kafka container when running with docker);


- Build the application running the gradle task `fatJar` with `./gradlew fatJar` on Linux/macOS or `gradlew fatJar` on Windows with jdk17+;


- Run `docker compose up` to download the necessary images and run all containers.;

Now you can send messages to the input topic from a different producer and consume the processed messages from the output topic in a different consumer assuming they're connected to the same kafka broker.

Note: the application won't respond to the same client with the same ip for 30 minutes, this is configurable in the .env files with the key DEFAULT_CACHE_MAX_AGE and the value in seconds.

Made by: Rafael Nunes Santana, July 2022
