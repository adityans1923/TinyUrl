Setup:
In docker-compose.yml
SPRING_SHORTURL_TTL: It's the amount of time in seconds shorturl generated is valid
SPRING_JANITOR_SCHEDULE: (in Second) Cron job scheduling period


To Run:
docker-compose up --build --scale tinyurl=3

Testing:

1. generate short url
2. get long url

curl -X POST -H "Content-Type: application/json" -H Host:server.localhost -d '{"longUrl":"121212"}'  http://server.localhost/generateShortUrl

curl -i -H Host:server.localhost http://server.localhost/c

Traefik Dashboard
http://localhost:22000/dashboard