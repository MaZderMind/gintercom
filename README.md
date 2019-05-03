# GIntercom

## Run (for Production)
Install Runtime-Dependencies (Tested on Debian Stretch):
```bash
apt install openjdk-8-jre 
apt install libgstreamer1.0-0 gstreamer1.0-plugins-base gstreamer1.0-plugins-good gstreamer1.0-plugins-bad
```

### Run Matrix
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
make package
ls -la {matrix,debugclient}/target/*.jar
```


## Run (for Development)
I recommend IntelliJ Ultimate (or Community, which lacks Spring Support but is otherwise still a great IDE). You won't get far with a Text-Editor.

Install Build-and Runtime-Dependencies (Tested on Debian Stretch):
```bash
apt install openjdk-8-jdk-headless maven make
apt install libgstreamer1.0-0 gstreamer1.0-plugins-base gstreamer1.0-plugins-good gstreamer1.0-plugins-bad
```

### Run Matrix Only
```bash
make debug-matrix
```
The Application will Render `.dot`-Files of the Pipeline-State as it changes to `matrix/matrix.dot`. USe `xdot` to view it.

The JRE will Hot-Reload the Application  when `.class`-Files are changed. Enable [Automatic Compilation](https://jrebel.com/software/jrebel/quickstart/intellij/enable-automatic-compilation-in-intellij-idea/) in your IDE. The Matrix will completely shutdown and restart. 

The Matrix will start a Debug-Server on Port `5010`. You can connect your IDE to these Ports to set Breakpoints and evaluate Code in the Context of the Application.

### Run Test-Scenario 
```bash
make debug-scenario
```
This will start a tmux-Session with a Matrix and multiple Instances of the GIntercom-Debug-Client, configured to interact in a Debug-Scenario. All Applications will Hot-Reload when changes to the different Parts are detected.

The Matrix will start a Debug-Server on Port `5010` and the GIntercom-Debug-Clients will each start Debug-Servers on Port `5011`, `5012`, … You can connect your IDE to one ore more of these Ports.

This is the preferred way to develop.
