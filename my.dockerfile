FROM openjdk:11
ADD /target/sigur.jar sigur.jar
ENTRYPOINT ["java","-jar","sigur.jar"]