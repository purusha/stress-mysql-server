rm -rf target/*.jar
mvn compile assembly:single
ls -l target/
