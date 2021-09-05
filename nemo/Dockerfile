FROM openjdk:8

ADD target/nemo.jar /usr/src/nemo/

COPY . /usr/src/nemo

# ADD openApi3.ttl /usr/src/nemo/
 
# ADD shacl.ttl /usr/src/nemo/

COPY ./Products /usr/src/nemo

COPY ./Descriptions /usr/src/nemo

WORKDIR /usr/src/nemo

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "nemo.jar"]

