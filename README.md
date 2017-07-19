MARY TTS README
===============

This is the source code repository for the HTTP Server RESTFUL wrapper of the multilingual open-source MARY text-to-speech platform (MaryTTS).  **MaryTTS can be found on [this page](https://github.com/marytts/marytts/)**.

The code comes under the Lesser General Public License LGPL version 3 -- see LICENSE.txt for details.


Running
-------

To run the server you just have to execute this command in a terminal:

```
./gradlew bootRun
```

You can access to the server through this address:
```
http://localhost:59125
```

Documentation
-------------
The MaryTTS HTTP Server comes with nicely formatted REST API documentation. To access the documentation, first build the project with following command:
```
./gradlew build
```
Then navigate to `build/asciidocs/html5` folder and double-click on `index.html` to open the documentation in your browser. You can also access the documentation by running the `jar` file which is created during project build. To do so, navigate to `build/libs` folder and run the jar file with following command:
```
java -jar marytts-http-server-{version}.jar
```
You can then access the documentation through following URL in your browser:
```
http://localhost:59125/docs/index.html
```

Logging
-------
MaryTTS http server uses Log4j2 for logging. By default, it logs the output to the console. If you want to override the default configuration, you can specify a custom Log4j2 XML file while starting the server like following:
```
./gradlew bootRun -Pargs="<complete-path-to-log4j2-xml-file>"
```
[Learn more](http://mycuteblog.com/log4j2-xml-configuration-example/) about creating a valid Log4j2 XML file or look into `src/main/resources/log4j2-test.xml` for an example.
