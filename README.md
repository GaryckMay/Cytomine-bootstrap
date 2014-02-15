# Cytomine Core

## Prerequisites

This module requires these dependencies :
* PostgreSQL >= 9.2
* Postgis >= 1.5
* Grails >= 2.3.5

### Install PostgreSQL

#### On Max OS X

First, install Homebrew (http://brew.sh/) and follow the instructions : 
 ```bash
ruby -e "$(curl -fsSL https://raw.github.com/Homebrew/homebrew/go/install)"
 ```
then install Postgis (will instal PostgreSQL as a dependencies):
 ```bash
brew install postgis 
 ```
	
#### On Linux (Ubuntu/Debian)

 ```bash
apt-get install postgis
 ```

### Install Grails

Install GVM (http://gvmtool.net/)
 ```bash
curl -s get.gvmtool.net | bash
 ```
 Install Grails :
 ```bash
gvm install grails 2.3.5
 ```
 
## Init databases 

These are different possible environment while running Cytomine Core 
* cytomine : dev environment
* cytomineempty : scratch environment (dev environment with an empty database at start)
* cytomineprod : production environment
* cytominetest : test environment (for running integration tests)

First, locate 'postigs.sql' and 'spatial_ref_sys.sql' on your system.
Then create all these 4 database
 ```bash
 createdb $DATABASE_NAME;
 psql $DATABASE_NAME -f $PATH_TO_POSTIG_SQL;
 psql $DATABASE_NAME -f $PATH_TO_SPATIAL_REF_SYS_SQL;
  ```
  
Example on Os X for Scratch environment :
 ```bash
 createdb cytomineempty;
 psql cytomineempty -f /usr/local/Cellar/postgis20/2.0.4/share/postgis/postgis.sql;
 psql cytomineempty -f /usr/local/Cellar/postgis20/2.0.4/share/postgis/spatial_ref_sys.sql;   
```
## Run Cytomine Core

#### In dev mode :
```bash
export GRAILS_OPTS="-Xmx1G -Xms256m -XX:MaxPermSize=256m -server"
grails -Dserver.port=8080 run-app
```

####  In scratch mode :
```bash
# free cytominempty
dropdb cytomineempty;
createdb cytomineempty;
psql cytomineempty -f $PATH_TO_POSTIG_SQL;
psql cytomineempty -f $PATH_TO_SPATIAL_REF_SYS_SQL;
# start grails with scratch environment
export GRAILS_OPTS="-Xmx1G -Xms256m -XX:MaxPermSize=256m"
grails  -Dgrails.env=scratch -Dserver.port=8080 run-app
```

####  In prod mode :
```bash
export GRAILS_OPTS="-Xmx1G -Xms256m -XX:MaxPermSize=256m"
grails prod -Dserver.port=8080 run-app
```

####  In test mode :
```bash
export GRAILS_OPTS="-Xmx1G -Xms256m -XX:MaxPermSize=256m -server -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog -Dorg.apache.commons.logging.simplelog.showdatetime=true -Dorg.apache.commons.logging.simplelog.log.org.apache.http=DEBUG"
grails -Dserver.port=8090 test-app functional:functional -echoOut -coverage
```