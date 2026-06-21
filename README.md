# Object Graph Visualizer v.3.1

![alt tag](https://github.com/Nurtak/ObjectGraphVisualization/blob/master/src/main/resources/images/OGV.png?raw=true)

## Description
Object Graph Visualizer is a tool primarily meant to be used in CS courses to help new students understand the Object Oriented paradigm and patterns. Classes and objects - that can be part of a software project - are visualised in 3D: The classes stay In the xz-plane in form of an UML class diagram. Directly above in the y-axis objects can be instantiated as an object diagram. Classes can be connected with all sorts of relations (Associations, Compositions, Generalization, etc.), objects with object relations.
Following additional functionality is provided: Attributes, Mulitplicities, Roles, Object Graph Mode, Coloring, Save and Load, XMI 1.1 Import from Enterprise Architect, Associations (undirected, directed, bidirected), Aggregations, Compositions, Dependency, Generalization, Multiple Virtual Inheritance

Developer Addendum: It shouldn't be too difficult to add an API for remote application control, e.g. visualize and debug a running programm. See class "ModelViewConnector". Any Contribution is welcome.

## Features
* Use Case: Educational, Software Engineering, UML
* 3D Visualization
* Save / Load Project
* XMI Import (Enterprise Architect XMI v.1.1)
* Create Class Diagramm
* Create Object Diagramm
* View Object Graph
* Add / Edit / Remove Classes, Objects, Relations, Attributes, Values, Multiplicities, Roles, ...
* Simulate Inheritance (virtual multiple)
* Free camera placement (rotational)
* Choose color for Classes, Objects, Relations, Background
* OS: Windows, Mac, Linux
* Runtime: Java 8u45
* Paradigm: Object Oriented Programming

## Release
[Object Graph Visualizer Version 3.1](https://github.com/Nurtak/ObjectGraphVisualization/releases)

The Java Runnable requires [Java 8u45]( https://www.java.com/de/download/).
The Windows Installer and Standalone come prepacked with the runtime.
More information and installation can be found in the [Instruction Manual](https://github.com/Nurtak/ObjectGraphVisualization/releases/download/v3.1/Instruction.Manual.pdf).

In depth thesis is available in German: http://eprints.hsr.ch/459/

## Screenshots
![alt tag](https://a.fsdn.com/con/app/proj/ogvisualizer/screenshots/screenshot1.PNG)

![alt tag](https://a.fsdn.com/con/app/proj/ogvisualizer/screenshots/screenshot2.PNG)

![alt tag](https://a.fsdn.com/con/app/proj/ogvisualizer/screenshots/screenshot3.PNG)

## Build and Run

### Install fxyzlib 0.3.0-patched
```
mvn install:install-file -Dfile=lib/fxyzlib-0.3.0-patched.jar -DgroupId=org.fxyz -DartifactId=fxyzlib -Dversion=0.3.0-patched -Dpackaging=jar`
```

### Install objmodelimporterjfx 0.8
```
mvn install:install-file -Dfile=lib/objmodelimporterjfx-0.8.jar -DgroupId=com.interactivemesh -DartifactId=objmodelimporterjfx -Dversion=0.8 -Dpackaging=jar`
```

### Build
```
mvn clean install
```

### Run (classpath)
```
java -jar --enable-native-access=ALL-UNNAMED ogv-3.3.0-runnable.jar
```

### Run (modulepath)
bash:
```bash
java --module-path "target/classes:target/lib:target/dependency" \
     --add-modules javafx.controls,javafx.fxml,javafx.graphics \
     --enable-native-access=javafx.graphics \
     -m ch.hsr.ogv/ch.hsr.ogv.Main
```

cmd:
```cmd
java --module-path "target/classes;target/lib;target/dependency" ^
     --add-modules javafx.controls,javafx.fxml,javafx.graphics ^
	 --enable-native-access=javafx.graphics ^
	 -m ch.hsr.ogv/ch.hsr.ogv.Main
```