FROM nathonfowlie/centos-jre:1.8.0_60
WORKDIR /app
COPY /target/case-yellow-central-0.0.1-SNAPSHOT.jar /app
CMD java -jar case-yellow-central-0.0.1-SNAPSHOT.jar
