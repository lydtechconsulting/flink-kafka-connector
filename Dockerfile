FROM openjdk:17.0.2-slim
ARG JAR_FILE=target/flink-kafka-connector-*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar ${APP_ARGS}"]
