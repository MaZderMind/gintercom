MATRIX_DEBUG_PORT = 5010
CLIENT_DEBUG_PORT ?= 5011
NG=./node_modules/.bin/ng

clean:
	mvn clean

package: ui-prod
	mvn package -DskipTests=true

dist: clean package
	rm -rf dist
	mkdir -p dist
	cp matrix/target/matrix-*.jar dist/matrix.jar
	cp debugclient/target/debugclient-*.jar dist/debugclient.jar
	cd dist && sha1sum *.* >SHA1SUMS
	cd dist && sha256sum *.* >SHA256SUMS

test:
	make ui-lint ui-test test-integration

test-integration:
	mvn verify

# run
run-tmux:
	tmux new-session \; source-file "$$PWD/run-split-tmux"

run-matrix:
	GST_DEBUG_DUMP_DOT_DIR=./matrix/target/ mvn --projects matrix --also-make spring-boot:run \
		-Dspring-boot.run.arguments="--config-directory=./matrix/example-config" \
		-Dspring-boot.run.jvmArguments="-Xdebug-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$(MATRIX_DEBUG_PORT)"

run-debugclient:
	GST_DEBUG_DUMP_DOT_DIR=./debugclient/ mvn --projects debugclient --also-make spring-boot:run \
		-Dspring-boot.run.arguments="--host=127.0.0.1,--port=8080,--host-id=0000-0001" \
		-Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$(CLIENT_DEBUG_PORT)"

# run `make package` first, then execute the prod-builds
run-packaged-matrix:
	java -jar matrix/target/matrix-*.jar --config-directory=./matrix/example-config

run-packaged-debugclient:
	java -jar debugclient/target/debugclient-*.jar

# ui-targets
ui-install: matrix/src/ui/node_modules

matrix/src/ui/node_modules: matrix/src/ui/package.json matrix/src/ui/package-lock.json
	cd matrix/src/ui && npm install

ui-run: ui-install
	cd matrix/src/ui && $(NG) build --watch=true

ui-lint: ui-install
	cd matrix/src/ui && $(NG) lint --type-check

ui-test: ui-install
	cd matrix/src/ui && $(NG) test --watch=false

ui-test-watch: ui-install
	cd matrix/src/ui && $(NG) test --watch=true

ui: ui-install
	cd matrix/src/ui && $(NG) build
	make ui-rename-es2015-chunks

ui-prod: ui-install
	cd matrix/src/ui && $(NG) build --prod
	make ui-rename-es2015-chunks

ui-rename-es2015-chunks:
	for chunk in main polyfills runtime vendor ; do \
		mv matrix/src/main/resources/public/ui-app/$$chunk-es2015.js \
			matrix/src/main/resources/public/ui-app/$$chunk.js; \
		mv matrix/src/main/resources/public/ui-app/$$chunk-es2015.js.map \
			matrix/src/main/resources/public/ui-app/$$chunk.js.map || true; \
	done

.PHONY: dist

