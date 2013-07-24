The OSIAM Connector for Java is under construction at the moment. 
If you are interested top use the OSIAM connector4java please watch us.

#OSIAM Connector for Java

This repository contains the OSIAM Connector for Java.

OSIAM gives you the possibility to have a easy oauth2 standard login of users.
It is also possible to asign Users to Groups with different roles.

It is possible to have your own OSIAM User and Group Server or to connect/share to a existing one.

OSIAM provides you 3 different ways for login
2 Step login: Your application redirects the user to a central Osiam Sever where the User can login and also grants your page to get access to the needed user information
1 Step login: The User can provide his login and password on your page and you can get a accesstoken direct from the OSIAM server without the authorization from the user
direct login: It is possible to get a accesstoken direct from the OSIAM server without the need of a user data


OSIAM is not depending on any language.
It is
using the oauth2 standard
RESTFUL
using scim User to provide Json based User Information

The connector4java gives you the possibility to easy use OSIAM from a Java enviroment.
It provides a easy to use API, a step to step documentation how to set up a testsystem and also provides different “ready to go” examples to be able to have a good understanding in a short time.

 For more information and documentation visit the [repo's Wiki](https://github.com/osiam/connector4java/wiki).

For more information on OSIAM have a look into the [OSIAM server repository's README.md](https://github.com/osiam/server/) or visit OSIAM's homepage at [www.osiam.org](https://www.osiam.org).

#project structure

This project gets build with maven, the module structure is

* ... -- is the main project, the request based OSIAM connector for Java itself -> Wiki

# Requirements

* Java 1.7

# Issue tracker for the Connector

Issues, bugs and feature requests can be brought to us using [OSIAM Connector for Java's issue tracker](https://github.com/osiam/connector4java/issues).
