First release of java swing SqlClient. More documentation to come.
October 19, 2016
Contact author at: gandersonsw@gmail.com

Required jars:
   commons-net-1.4.0.jar
   jdom.jar
   mysql-connector-java-5.1.22-bin.jar    (for mysql databases)
   ojdbc14-10.2.0.2.0.jar                 (for oracle databases - drop into libs directory, make sure build.gradle references correct file name)
   ojdbc14.jar                            (for SqlServer databases)

Command to build and run:
gradle -q run

Command to build jar:
gradle -q jar


Command to create jar file:
jar cfm SqlClient.jar manifest.txt -C out/production/lapae com/graham

Command to run jar from command line:
java -jar SqlClient.jar


