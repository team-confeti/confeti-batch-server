FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY build/libs/confeti-batch-server-*.jar /app/confeti.jar

ENV JAVA_OPTS="-Xmx2048m"

CMD ["sh", "-c", "java $JAVA_OPTS -Duser.timezone=Asia/Seoul -jar -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE confeti.jar"]
