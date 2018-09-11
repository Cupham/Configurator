# EchonetLiteResourceManager
This is a echonet Lite home gateway which support semantic reasoning to manage home network resource using Jena framework

How To
A. Use EchonetInterface library as a maven dependency
   1. Install into local maven repository

    mvn install:install-file -Dfile=EchonetLiteInterface.jar -DgroupId=makino.echowand -DartifactId=echonetlite -Dpackaging=jar -Dversion=0.1
   
   2. add dependency to pom file

     <dependency>
		<groupId>makino.echowand</groupId>
		<artifactId>echonetlite</artifactId>
		<version>0.1</version>
     </dependency>

