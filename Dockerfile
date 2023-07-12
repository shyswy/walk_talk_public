FROM --platform=linux/amd64 amazoncorretto:11
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} clubSite.jar
ENTRYPOINT ["java","-jar","/clubSite.jar"]

#FROM --platform=linux/amd64 amazoncorretto:11
#ARG JAR_FILE=build/libs/*.jar
#COPY ${JAR_FILE} clubSite.jar
#COPY ./src/main/resources/application-pr.properties /
#COPY ./src/main/resources/application-dv.properties /
#COPY ./src/main/resources/application-st.properties /
#ENTRYPOINT ["java","-jar","/clubSite.jar"]

#FROM --platform=linux/amd64 amazoncorretto:11
#
#ARG JAR_FILE=build/libs/*.jar
#ARG CONFIG_FILE
#
#COPY ${JAR_FILE} clubSite.jar
#COPY ./src/main/resources/application-${CONFIG_FILE}.properties /config/application.properties
#
#ENTRYPOINT ["java","-jar","/clubSite.jar","--spring.config.location=/config/application.properties"]


