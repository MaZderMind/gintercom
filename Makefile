MATRIX_DEBUG_PORT = 5010
CLIENT_DEBUG_PORT ?= 5011

clean:
	mvn clean

package:
	mvn package

test:
	mvn test

# run development
run-matrix:
	GST_DEBUG_DUMP_DOT_DIR=./matrix/ mvn --projects matrix --also-make spring-boot:run -Dspring-boot.run.arguments="--config-directory=./matrix/example-config"

run-debugclient:
	mvn --projects debugclient --also-make spring-boot:run

# debug development
debug-matrix:
	GST_DEBUG_DUMP_DOT_DIR=./matrix/ mvn --projects matrix --also-make spring-boot:run -Dspring-boot.run.arguments="--config-directory=./matrix/example-config" -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$(MATRIX_DEBUG_PORT)"

debug-debugclient:
	mvn --projects debugclient --also-make spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$(CLIENT_DEBUG_PORT)"

# run `make package` first, then execute the prod-builds
run-packaged-matrix:
	GST_DEBUG_DUMP_DOT_DIR=./matrix/ java -jar matrix/target/matrix-*.jar --config-directory=./matrix/example-config

run-packaged-debugclient:
	java -jar debugclient/target/debugclient-*.jar
