**JavaFX 2 desktop application for cepgate**

Maven plugin _javafx-maven-plugin_ is used to build and package the application.

To run application, execute maven goal:
**mvn jxf:run**
After build, the application is packaged into executable jar located at ${project.basedir}/target/jfx/app. It needs dependencies located in lib directory along the jar. The application may be started with _java -jar_ command.

To create native distribution of app, use maven goal:
**mvn jfx:native**
Note, that thought javaFX is multiplatform, the native package can be created only for current operating system. I.e. build app as linux package, the above command have to be run on linux os, to create MS Windows native app - on MS Windonws os etc. 
