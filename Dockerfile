FROM clojure:openjdk-8-tools-deps

RUN mkdir /app
WORKDIR /app
ADD ./ /app/

RUN clojure -A:dev:test -e "(do (println \"Downloaded dependencies\") (System/exit 0))"

VOLUME /app/

# TODO: Compiling js, running http server, running nrepl in dev mode