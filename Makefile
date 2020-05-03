MATRIX_DEBUG_PORT = 5010
CLIENT_DEBUG_PORT ?= 5011

clean:
	mvn clean

package:
	mvn package

dist: clean package
	rm -rf dist
	mkdir -p dist
	cp matrix/target/matrix-*.jar dist/matrix.jar
	cp debugclient/target/debugclient-*.jar dist/debugclient.jar
	cd dist && sha1sum *.* >SHA1SUMS
	cd dist && sha256sum *.* >SHA256SUMS

test:
	mvn verify

# run development
run-matrix:
	mvn --projects matrix --also-make spring-boot:run -Dspring-boot.run.arguments="--config-directory=./matrix/example-config"

run-debugclient:
	mvn --projects debugclient --also-make spring-boot:run

# debug development
debug-matrix:
	GST_DEBUG_DUMP_DOT_DIR=./matrix/ mvn --projects matrix --also-make spring-boot:run -Dspring-boot.run.arguments="--config-directory=./matrix/example-config" -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$(MATRIX_DEBUG_PORT)"

debug-debugclient:
	GST_DEBUG_DUMP_DOT_DIR=./debugclient/ mvn --projects debugclient --also-make spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$(CLIENT_DEBUG_PORT)"

# run `make package` first, then execute the prod-builds
run-packaged-matrix:
	java -jar matrix/target/matrix-*.jar --config-directory=./matrix/example-config

run-packaged-debugclient:
	java -jar debugclient/target/debugclient-*.jar

.PHONY: dist

