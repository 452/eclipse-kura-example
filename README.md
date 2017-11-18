# I2C scanner tool Java eclipse kura bundle

# How to open Kura GoGo shell
```sh
telnet 127.0.0.1 5002
```

# Install bundles from maven repo
```sh
install http://central.maven.org/maven2/org/apache/camel/camel-http-common/2.17.2/camel-http-common-2.17.2.jar
install http://central.maven.org/maven2/org/apache/camel/camel-servlet/2.17.2/camel-servlet-2.17.2.jar

install http://central.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.7.2/jackson-core-2.7.2.jar
install http://central.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.7.2/jackson-annotations-2.7.2.jar
install http://central.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.7.2/jackson-databind-2.7.2.jar
install http://central.maven.org/maven2/com/fasterxml/jackson/dataformat/jackson-dataformat-yaml/2.7.2/jackson-dataformat-yaml-2.7.2.jar
install http://central.maven.org/maven2/io/swagger/swagger-core/1.5.8/swagger-core-1.5.8.jar

install http://central.maven.org/maven2/org/apache/camel/camel-swagger-java/2.17.2/camel-swagger-java-2.17.2.jar
```

# How to build
```sh
mvn clean package
```

http://kura-server-device-ip/rest/list

```sh
i2cdetect -y 1
watch -n 0.1 i2cdump -y 1 0x77
```

#### Links
https://svn.apache.org/repos/infra/websites/production/camel/content/eclipse-kura-component.html
https://dzone.com/articles/apache-camel-iot-world-eclipse
http://henryk-konsek.blogspot.com/2015/02/?m=0
http://download.eclipse.org/kura/docs/api/3.1.0/apidocs
http://people.apache.org/~dkulp/camel/servlet.html