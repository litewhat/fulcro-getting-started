FROM clojure:openjdk-8-tools-deps

RUN mkdir /app
WORKDIR /app
ADD ./ /app/

RUN echo "Downloading dependencies..."
RUN clojure -Stree -A:dev:test:runner

CMD ["clojure", "-A:dev:test"]

# TODO: Compiling js, running http server, running nrepl in dev mode