# Dockerfile
FROM amazoncorretto:17
VOLUME /tmp

WORKDIR /app

ARG JAR_FILE=aqa-shop.jar
COPY app/${JAR_FILE} aqa-shop.jar
COPY entrypoint.sh entrypoint.sh

RUN chmod +x entrypoint.sh

ENTRYPOINT ["./entrypoint.sh"]

