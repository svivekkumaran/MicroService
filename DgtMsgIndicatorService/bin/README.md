# OMNI_RE_WEBAPP
WebApplication-ANT-SCXML Tomcat

All the applications in this repository uses the maven to compile and package the war files. All the config related files are in resource directory, one file for each environment as below :

application-DEV2.properties
application-QA.properties
application-STAGE.properties
application-PROD.properties

The environment variable will be picked up by the module while starting up, from the following file 

/apps/gcti/backup/DONOTDELETE_tomcat_env.file

This file contains the text which indicates the environment like DEV2/QA/STAGE

In the case of prod servers, this file will not be present. The absence of this env file indicates that the environment to be interpreted as PROD and the properties file (application-PROD.properties) will be read.

