FROM java:8
VOLUME /tmp
ADD target/secdev-0.0.1-SNAPSHOT.jar  secdev-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/secdev-0.0.1-SNAPSHOT.jar"]
