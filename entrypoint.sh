#!/bin/sh

if [ "$DB_TYPE" = "mysql" ]; then
  echo "Waiting 15s for MySQL initialization..."
  sleep 15
  export SPRING_DATASOURCE_URL="jdbc:mysql://mysql:3306/app?allowPublicKeyRetrieval=true&useSSL=false&connectTimeout=10000"
  export SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT="org.hibernate.dialect.MySQL8Dialect"
  JAVA_OPTS="-Ddb.type=mysql -Ddb.url=jdbc:mysql://mysql:3306/app?allowPublicKeyRetrieval=true&useSSL=false"
else
  echo "Waiting 15s for PostgreSQL initialization..."
  sleep 15
  export SPRING_DATASOURCE_URL="jdbc:postgresql://postgres:5432/app"
  export SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT="org.hibernate.dialect.PostgreSQL10Dialect"
  JAVA_OPTS="-Ddb.type=postgres -Ddb.url=jdbc:postgresql://postgres:5432/app"
fi

export SPRING_DATASOURCE_USERNAME="$DB_USER"
export SPRING_DATASOURCE_PASSWORD="$DB_PASSWORD"
export JAVA_OPTS="$JAVA_OPTS -Ddb.user=app -Ddb.password=pass"

exec java $JAVA_OPTS -jar /app/aqa-shop.jar
