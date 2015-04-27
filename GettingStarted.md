# Introduction #

There are a few steps take to get JC-Rest properly deployed.


# Details #

## JCR API JAR ##

I have commented out the JCR API dependency in the POM because that JAR is most likely already in your server shared libraries folder. With CRX, if the JAR is in the WAR and in your server shared libraries folder you will see a Spring error at start up because it was unable to convert the CRX repository implementation from the JNDI object to a JCR implementation. You can either add that JAR to your server shared libraries folder, or uncomment the dependency from the POM.

## Configure Repository Access ##

You will have to modify the WEB-INF/repository.properties file.

  * repository.jndi.name - The JNDI name for your repository.
  * repository.user.name - The username.
  * repository.user.password - The password.
  * repository.workspace.name - The repository workspace to connect to.

It is currently configured for a default Day Communique Author instance.


  * repository.jndi.name=crxauthor
  * repository.user.name=admin
  * repository.user.password=admin
  * repository.workspace.name=live\_author

## Configure Logging (Optional) ##

You will have to modify the WEB-INF/log4j.properties file.

  * log4j.appender.logfile.File - The location of the log file.

It is currently configured to be written to a location in the exploded web application directory. You'll likely want to specify a permanent location.