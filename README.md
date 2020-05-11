# GIntercom

## Run in Production
Install Runtime-Dependencies (Tested on [Debian Stretch](https://www.debian.org/releases/stretch/)):
```bash
apt install openjdk-8-jre
apt install libgstreamer1.0-0 gstreamer1.0-plugins-base gstreamer1.0-plugins-good gstreamer1.0-plugins-bad
```

## Build for Production
Install Build-Dependencies (Tested on [Debian Stretch](https://www.debian.org/releases/stretch/)):
```bash
apt install openjdk-8-jdk-headless maven make
```

GIntercom should also work flawlessly on OpenJDK 11.

Build all Packages
```bash
make clean package
ls -la {matrix,debugclient}/target/*.jar
```


## Run for Development
I recommend [IntelliJ Ultimate](https://www.jetbrains.com/idea/buy/#commercial?billing=monthly) (or 
[IntelliJ Community](https://www.jetbrains.com/idea/download/#section=linux), which lacks
Spring Support but is otherwise still a great IDE). You won't get far with a Text-Editor.

Install Build-and Runtime-Dependencies (Tested on [Debian Stretch](https://www.debian.org/releases/stretch/)):
```bash
apt install openjdk-8-jdk-headless maven make tmux
apt install libgstreamer1.0-0 gstreamer1.0-plugins-base gstreamer1.0-plugins-good gstreamer1.0-plugins-bad
```

### Run Matrix for Development
```bash
make clean run-tmux
```
This will start a [tmux](https://github.com/tmux/tmux/wiki)-Session with two panes.
On the Top you'll see the UI Build running and then waiting for changes.
On the bottom you'll see the Matrix-Application compiling and then starting to serve requests.

You can Access the Web-UI from your browser at http://localhost:8080/ and start Debug-Clients to test the Intercom-Functionality.

Changes made in the UI-Part under `matrix/src/ui/src` will be picked up by the UI-Build and available straight-away by reloading your
browser. Changes to the Java-Part will trigger an automatic Restart of the Matrix-Application, when you configure your IDE for
automatic re-compilation of changed files. See [Automatic Compilation in IntelliJ](https://jrebel.com/software/jrebel/quickstart/intellij/enable-automatic-compilation-in-intellij-idea/)
for IntelliJ. The Matrix will completely shutdown and restart, connected Clients will be disconnected and reconnect.

For additional convenience you can install the LiveReload-Extension for [Chrome](https://chrome.google.com/webstore/detail/livereload/jnihajbhpnppcggbcgedagnkighmdlei) 
or [Firefox](https://addons.mozilla.org/en-US/firefox/addon/livereload-web-extension/). SpringBoot starts an 
[LiveReload](http://livereload.com/api/protocol/)-Server to which the Browser-Extension connects. Whenever the UI or the Java-Part
changes, the browser will do an automatic reload. 

In Dev-Mode the Matrix will write `.dot`-Files as visualisations of the GStreamer Pipeline whenever it changes to `matrix/target/*.dot`. Use
`xdot` to view them (they can become quite large with multiple Groups and Panels). 

The JVM will start a Debug-Server on Port `5010`. You can connect your IDE to these Ports to set Breakpoints and evaluate Code in the
Context of the Matrix Application.

### Run Debug-Client for Development
The Debug-Client is a full-featured GIntercom-Client packaged as a Desktop-Application with additional Debug-Utilities like a Level-Meter,
a Tone-Generator and the option to enable and disable the Speaker and the Microphone. It can be launched multiple times on a single
development machine to simulate multiple Clients connected to the development Matrix. Besides the clunky UI and the special Audio
-Pipeline features, it is normal GIntercom-Client using all the Framework provided

```bash
make run-debugclient
```

This will build and start the Debug-Client configured as Panel `Helpdesk 1` and connected to the local development Matrix.

In Dev-Mode the Debug-Client will write `.dot`-Files as visualisations of the GStreamer Pipeline whenever it changes to `debug-client/target
/*.dot`. Use `xdot` to view them (they can become quite large with multiple Groups and Panels). 

The JVM will start a Debug-Server on Port `5011`. You can connect your IDE to these Ports to set Breakpoints and evaluate Code in the
Context of the Debug-Client Application.

### Run Test-Scenario
(!) Not yet implemented
```bash
make run-scenario
```
This will start a tmux-Session with a Matrix and multiple Instances of the GIntercom-Debug-Client, configured to interact in a Debug-Scenario. All Applications will Hot-Reload when changes to the different Parts are detected.

The Matrix will start a Debug-Server on Port `5010` and the GIntercom-Debug-Clients will each start Debug-Servers on Port `5011`, `5012`, … You can connect your IDE to one ore more of these Ports.

This is the preferred way to develop.
