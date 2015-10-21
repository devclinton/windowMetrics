windowMetrics
==============

Windows Metrics is a Java Based service that logs what process/windows you have active. It is intended to include a GUI for analysis and categorization. Currently, only the logging portion is working.

Building Window Metrics
------------------------

Window Metrics uses the Gradle build system. You should just be able to checkout a branch of the repo and then run

* **gradle oneJar** - This will create a Jar located at build/libs/windowMetrics-0.1-SNAPSHOT-standalone.jar

Additionally, an IntelliJ Project file has been included.

Running Window Metrics
-------------------------

Once you have a jar from the build, you will need to src/dist/config.yml to your needs. Most likely your database configuration will change. TODO: Add example DB configs for common DBSs

You can now launch the service using your config like so

*java -jar windowMetrics-0.1-SNAPSHOT-standalone.jar server /path/to/config.yml*