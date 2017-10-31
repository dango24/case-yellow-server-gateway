FROM nathonfowlie/centos-jre:1.8.0_60
WORKDIR /app
COPY /target/case-yellow-central-0.0.1-SNAPSHOT.jar /app
RUN ["apt-get", "update"]
RUN ["apt-get", "install", "-y", "vim"]
CMD java -jar case-yellow-central-0.0.1-SNAPSHOT.jar
