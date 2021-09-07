FROM hseeberger/scala-sbt:11.0.12_1.5.5_2.13.6
WORKDIR /www/app

COPY ./ ./

RUN sbt compile

EXPOSE 8080
ENTRYPOINT ["sbt", "run"]
