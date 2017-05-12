MARY TTS README
===============

This is the source code repository for the http server RESTFUL wrapper of the multilingual open-source MARY text-to-speech platform (MARY TTS).  **MARY TTS can be find on [this page](https://github.com/marytts/marytts/)**.

The code comes under the Lesser General Public License LGPL version 3 -- see LICENSE.txt for details.


Running
-------

To run the server you just have to execute this command in a terminal:

```
./gradlew bootRun
```

You can access to the server through this address
```
http://localhost:59125
```

Logging
-------
MaryTTS http server uses Log4j for logging. By default, it logs the output to the console. If you want to override the default configuration, you can specify a custom Log4j properties file while starting the server like following:
```
./gradlew bootRun -Pargs="<complete-path-to-properties-file>"
```
[Learn more](https://www.mkyong.com/logging/log4j-log4j-properties-examples/) about creating a valid Log4j properties file or look into `src/main/resources/mary-log4j.properties` for an example.
