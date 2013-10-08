Just A Simple Store Server
=====================================

This is the implementation of a store server.
The store is used with the iCasa Platform and it serves to store iCasa applications information.

Features 
----------
* A product is a set of applications, devices, and subscriptions.
* Only the applications are installable entities, device and subscriptions are (for instance) for information purpose.


Requisites:
--
* MySQL 5.2
* MySQL workbench (recommended) to administrate the data base.
* Maven 3
* Java 7
* Play2 framework
* A google account.
* Internet access.

Installation
--
* Make sure the MySQL server is launched.
* Launch the script **appstore.sql** located in the **$project-folder$/sql/** folder.
    * You can use the mysql command
    
        `$mysql < appstore.sql`
        
    * Or you can MySQL workbench to do it (recommended).
    
* To configure user and password, modify the file **application.conf** located in **$project-folder$/conf/** and modify these two lines:

    `db.default.user=appstore`
    
    `db.default.password=1234`
    
* using MySQL workbench or the mysql shell, add a user and grant access to the appstore database. User and password must match with the one used in the **application.conf** file.

* Run using maven
    * $mvn play2: start

Execution
---------
* go to http://localhost:9000  
* or go to http;//localhost:9000/admin
* This server use the google account to login to the application.

