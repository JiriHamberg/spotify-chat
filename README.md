# spotify-chat
A chat built on top of Spotify API where the chat room is determined by the song you are listening to.

# Prerequisite
Create and reqister a new Spotify app following instructions at https://developer.spotify.com/documentation/general/guides/app-settings/

For your app, register a callback URI: 
http://localhost:3000/callback/spotify-auth

Create a file named .env in the project root repository with following content:

```
CHAT_CLIENT_ID=<your client id>
CHAT_CLIENT_SECRET=<your client secret>
CHAT_CLIENT_FRONTEND_URL=http://localhost:3000
CHAT_DOMAIN_NAME=localhost

STOMP_RELAY_HOST=rabbitmq
STOMP_RELAY_PORT=61613
STOMP_RELAY_USERNAME=guest
STOMP_RELAY_PASSWORD=guest
STOMP_RELAY_VIRTUAL_HOST=
```
("your client id" should be your Spotify App's id and "your client secret" should be your Spotify App's client secret.)


# Build
`git submodule update --init --recursive`

`docker-compuse build`

# Run
`docker-compose up`
