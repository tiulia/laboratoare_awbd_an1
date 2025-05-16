FROM mongo:latest
COPY ./samplefiles/movies.json /movies.json
COPY ./samplefiles/comments.json /comments.json
CMD mongoimport  --host mongo_test  --username umovies --password pmovies --db moviesdb --collection movies --type json --jsonArray --file /movies.json
