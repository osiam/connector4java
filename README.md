For detailed information and documentation visit the [connector4java Wiki](https://github.com/osiam/connector4java/wiki).

If you are interested to use the OSIAM connector4java please watch or, even better, join us on Github.

#Overview

This repository contains the OSIAM Connector for Java.

The current version can be found at http://search.maven.org/#search|ga|1|osiam.

Together with the OSIAM server it provides an easy to use OAuth2 SCIM based user login for Java.

#Short OSIAM introduction

OSIAM provides an easy and secure oauth2 standard login of users. It is also possible to asign users to groups with different roles.

It is possible to run a dedicated OSIAM User and Group Server or to connect to an existing identitiy provider (like login over facebook).

OSIAM provides three different ways to log in:

- 2 Step login: The consuming application redirects the user to a central Osiam Sever where the user can login and the consuming applications granted access to the needed user information (for instance if the facebook acount of the user is used for login)

- 1 Step login: The user can provide his login and password to your application and you can get the security credentials directly from the OSIAM server without a required authorization from the user

- direct login: It is possible to get a security credentials directly from the OSIAM server without the need of any user data

The different ways to login depend on the configured grants on the OSIAM server.


OSIAM does not depend on any programming language.

It is

- using the oauth2 standard

- RESTful

- using scim to provide Json based user management

#OSIAM Connector for Java

The connector4java enables easy and convenient use of OSIAM authentication and user management in a Java enviroment.

It provides a easy to use API, a step by step documentation how to set up a test system and also offers different “ready to go” examples that should enable the  understanding of OSIAM and the connector4java in a short time.

For more information on OSIAM have a look into the [OSIAM server repository's README.md](https://github.com/osiam/server/) or visit OSIAM's homepage at [www.osiam.org](https://www.osiam.org).

#project structure

This project is build with maven, the module structure is

* ... -- is the main project, the request based OSIAM connector for Java itself. Please see the Wiki.

# Requirements

* Java 1.7

# Issue tracker for the Connector

Issues, bugs and feature requests can be reported by using [OSIAM's issue tracker](https://jira.osiam.org/browse/BT).
