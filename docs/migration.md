# from 0.15 to 1.0
* Instead of setting the authentication information in the
OsiamConnector.Builder, you have to set them in the retrieveAccessToken method.
Beware of using the retrieveAccessToken without params. In this case, you get a
client access token without any scopes.
* The StringQueryBuilder changed to QueryBuilder, please check the javadoc or
documentation for more information how to use it.

# from 0.14 to 0.15
* The method setAuthServiceEndpoint in the OsiamConnector.Build renamed to
setAuthServerEndpoint
* The method setResourceEndpoint in the OsiamConnector.Build renamed to
setResourceServerEndpoint

# from 0.13 to 0.14
* No migration needed

# from 0.12 to 0.13
* UpdateUser and UpdateGroup moved to scim schema project
* Update the user only possible with UpdateUser
* Scim User and UpdateUser can handle extensions

# from 0.11 to 0.12
The replaceUser method of the OsiamConnector now also has the uuid of the user
that will be replaced as first parameter, like the updateUser method:
replaceUser(user, accessToken) to replaceUser(uuid, user, accessToken)
