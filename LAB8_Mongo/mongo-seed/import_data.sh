mongoimport --host mongo_test --username umovies --password pmovies --db moviesdb --collection movies --type json --jsonArray --file /movies.json
mongoimport --host mongo_test --username umovies --password pmovies --db moviesdb --collection embedded_movies --type json --jsonArray --file /embedded_movies.json
mongoimport --host mongo_test --username umovies --password pmovies --db moviesdb --collection comments --type json --jsonArray --file /comments.json
