To Run: 

docker-compose up --build --scale tinyurl=5

1. generate short url
2. get long url


curl -X POST -H "Content-Type: application/json" -H Host:server.localhost -d '{"longUrl":"121212"}'  http://server.localhost/generateShortUrl

curl -i -H Host:server.localhost http://server.localhost/c