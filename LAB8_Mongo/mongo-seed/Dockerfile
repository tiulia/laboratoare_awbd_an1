FROM mongo:latest
COPY ./samplefiles/movies.json /movies.json
COPY ./samplefiles/embedded_movies.json /embedded_movies.json
COPY ./samplefiles/comments.json /comments.json

COPY import_data.sh /import_data.sh
RUN chmod +x /import_data.sh

CMD ["/import_data.sh"]

