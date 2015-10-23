The Connector4java is a client for [OSIAM](https://github.com/osiam/osiam)
and provides an easy to use Java API that enables user logins or the
administration of users, groups and their interactions.

OSIAM provides a time limited access token that regulates the access to
resources at any one time. For this purpose the Connector4java API enables an
easy way to retrieve this token on different ways depending on your rights in
OSIAM.

More information about the login, retrieving an access token and the possible
grants can be found [here](login-and-getting-an-access-token.md).

# What can be done with the Connector?

## [Login](login-and-getting-an-access-token.md#login)

- Login by providing user name and password of an user, if you want to have
your own login page

## [Access token](login-and-getting-an-access-token.md#retrieving-an-accesstoken)

Retrieval of information:
- access token after the user's or client's login
- if the actual access token is still valid
- the scopes (rights) of your actual access token

## [User](working-with-user.md)

Retrieval of
- single users, lists of users and the actual logged-in users

## [Group](working-with-groups.md)

Retrieval of
- single groups, lists of groups and members of a group
