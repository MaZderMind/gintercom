# GIntercom

## Run (for Production)
Install Runtime-Dependencies (Tested on Debian Stretch):
```bash
apt install openjdk-8-jre
apt install libgstreamer1.0-0 gstreamer1.0-plugins-base gstreamer1.0-plugins-good gstreamer1.0-plugins-bad
```

### Run Matrix (for Development)
```bash
java -jar matrix-0.0.1-SNAPSHOT.jar -c matrix/example-config/
```

### Run Debug-Client
```bash
java -jar debugclient-0.0.1-SNAPSHOT.jar
```

## Build (for Production)
Install Build-Dependencies (Tested on Debian Stretch):
```bash
apt install openjdk-8-jdk-headless maven make
```

Build all Packages
```bash
make clean package
ls -la {matrix,debugclient}/target/*.jar
```


## Run (for Development)
I recommend IntelliJ Ultimate (or Community, which lacks Spring Support but is otherwise still a great IDE). You won't get far with a Text-Editor.

Install Build-and Runtime-Dependencies (Tested on Debian Stretch):
```bash
apt install openjdk-8-jdk-headless maven make tmux
apt install libgstreamer1.0-0 gstreamer1.0-plugins-base gstreamer1.0-plugins-good gstreamer1.0-plugins-bad
```

### Run Matrix
```bash
make clean run-tmux
```
This will start a tmux-Session with two panes.
On the Top you'll see the UI Build running and then waiting for changes.
On the bottom you'll see the Matrix-Application compiling and then starting to serve requests.

You can Access the Web-UI from your browser at http://localhost:8080/ and start Debug-Clients to test the Intercom-Functionality.

Changes made in the UI-Part under `matrix/src/ui/src` will be picked up by the UI-Build and available straight-away by reloading your
browser. Changes to the Java-Part will trigger an automatic Restart of the Matrix-Application, when you configure your IDE for
automatic re-compilation of changed files. See [Automatic Compilation in IntelliJ](https://jrebel.com/software/jrebel/quickstart/intellij/enable-automatic-compilation-in-intellij-idea/)
for IntelliJ. The Matrix will completely shutdown and restart, connected Clients will be disconnected and reconnect.

In Dev-Mode the Matrix will write `.dot`-Files as visualisatons of the GStreamer Pipeline whenever it changes to `matrix/target/*.dot`. Use
`xdot` to view them (they can become quite large with multiple Groups and Panels). 

The JVM will start a Debug-Server on Port `5010`. You can connect your IDE to these Ports to set Breakpoints and evaluate Code in the
Context of the Application.

### Run Debug-Client
```bash
make run-debugclient
```

This will build and start the Debug-Client configured as Panel `Helpdesk 1` and connected to the local development Matrix.


### Run Test-Scenario 
```bash
make debug-scenario
```
This will start a tmux-Session with a Matrix and multiple Instances of the GIntercom-Debug-Client, configured to interact in a Debug-Scenario. All Applications will Hot-Reload when changes to the different Parts are detected.

The Matrix will start a Debug-Server on Port `5010` and the GIntercom-Debug-Clients will each start Debug-Servers on Port `5011`, `5012`, … You can connect your IDE to one ore more of these Ports.

This is the preferred way to develop.
