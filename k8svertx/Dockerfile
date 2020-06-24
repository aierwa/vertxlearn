FROM fabric8/java-alpine-openjdk8-jre

EXPOSE 8080

# copy dependencies
COPY target/dependency/* /deployment/libs/

# copy classed
COPY target/classes /deployment/classes

ENV JAVA_APP_DIR=/deployment
ENV JAVA_LIB_DIR=/deployment/libs
ENV JAVA_CLASSPATH=${JAVA_APP_DIR}/classes:${JAVA_LIB_DIR}/*
ENV JAVA_MAIN_CLASS="com.xuxiang.vertx.k8svertx.GreetingApplication"