run:
	GST_DEBUG_DUMP_DOT_DIR=. mvn spring-boot:run -Dspring-boot.run.arguments="--config-directory=./example-config"

clean:
	mvn clean

package: clean
	mvn package

package-run: package
	GST_DEBUG_DUMP_DOT_DIR=. java -jar target/matrix-*.jar -config-directory=./example-config
