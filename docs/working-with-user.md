To perform any actions with users you always need an access token. For
retrieving an access token please see [Login and getting an access token](login-and-getting-an-access-token.md).

The user class that will be used by OSIAM and the Java Connector is org.osiam.resources.scim. **User**.
Detailed information about this class can be found at the [scim-schema]
(https://github.com/osiam/scim-schema/blob/master/docs/api/user.md).

Chapters:
- [User Object](#user)
- [Create a User](#create-a-user)
- [Delete a User](#delete-a-user)
- [Update a User](#update-a-user)
- [Retrieve a single User](#retrieve-a-single-user)
- [Retrieve the currently logged in User by his access token](#retrieve-the-current-logged-in-user-by-his-access-token)
- [Retrieve all User](#retrieve-all-users)
- [Search for Users](#search-for-user)

# User

The OSIAM Connector4Java is working with org.osiam.resources.scim. **User**
Objects.

A new user can be created with his builder.

```java
User user = new User.Builder("<userName>")
                       .setPassword("<password>")
                       //...
                       .build();
```

Some variables of a user are:

```java
String userName = user.getUserName();
String userId = user.getId(); //UUID
List<Address> addresses = user.getAddresses();
List<Email> emails = user.getEmails();
String password = user.getPassword(); // will always be null
//...
```

# Create a User

After you have created a new user you can save him with:

```java
OsiamConnector oConnector = [Retrieving an OsiamConnector](create-osiam-connector.md)
AccessToken accessToken = [Retrieving an AccessToken](login-and-getting-an-access-token.md#retrieving-an-accesstoken)
User newUser = [create a new User](#user)
newUser = oConnector.createUser(newUser, accessToken);
```

Now the returned user object contains the ID and meta data of the newly created
user.

# Delete a User

To delete a user call:

```java
OsiamConnector oConnector = [Retrieving an OsiamConnector](create-osiam-connector.md)
AccessToken accessToken = [Retrieving an AccessToken](login-and-getting-an-access-token.md#retrieving-an-accesstoken)
oConnector.deleteUser(<USER_UUID>, accessToken);
```

# Update a User

The OSIAM Connector4Java provides an easy and quick way to change user data,
whether you just want to update a single value or almost all of them.
It is easy to change data, even if you want to change the address of all your
employees because your company moved.

The following examples show how you can use the update method:

```java
OsiamConnector oConnector = [Retrieving an OsiamConnector](create-osiam-connector.md)
AccessToken accessToken = [Retrieving an AccessToken](login-and-getting-an-access-token.md#retrieving-an-accesstoken)
```

If you want to update a user you need to know his ID (UUID). Get the ID with:

    User existingUser = [get existing User](#retrieve-a-single-user)

Now you can create a UpdateUser and change his attributes:

    UpdateUser updateUser = [Creating an UpdateUser](https://github.com/osiam/scim-schema/blob/master/docs/api/update-user.md)

After this you just have to call the UpdateUser method which will return the
complete updated user:

    User updatedUser = oConnector.updateUser(existingUser.getId(), updateUser, accessToken);

If you want to change the same attributes for several users you can call the
method several times with different user ID's and the same UpdateUser Object.


```java
User updatedUser01 = oConnector.updateUser(userId01, updateUser, accessToken);
User updatedUser02 = oConnector.updateUser(userId02, updateUser, accessToken);
User updatedUser03 = oConnector.updateUser(userId03, updateUser, accessToken);
```

# Retrieve a single User
   
To retrieve a single user you need his UUID (for example:
94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4):

```java
OsiamConnector oConnector = [Retrieving an OsiamConnector](create-osiam-connector.md)
AccessToken accessToken = [Retrieving an AccessToken](login-and-getting-an-access-token.md#retrieving-an-accesstoken)
User user = oConnector.getUser(<USER_UUID>, accessToken);
```

(please consider the possible runtimeException which are explained in the
Javadoc)

# Retrieve the current logged in user by his access token

If you are logged in with the client scope and you try to retrieve the current
logged user you will retrieve a ConflictException.

```sh
OsiamConnector oConnector = [Retrieving an OsiamConnector](create-osiam-connector.md)
AccessToken accessToken = [Retrieving an AccessToken](login-and-getting-an-access-token.md#retrieving-an-accesstoken)
// retrieves the basic data of the actual User
BasicUser basicUser = oConnector.getCurrentUserBasic(accessToken);
// retrieves the complete currently logged in User.
User user = oConnector.getCurrentUser(accessToken);
```

If you only need the basic User data like the userName or the Name, we would
recommend to use the getCurrentUserBasic method since this one is with more
performance.

(please consider the possible runtimeException which are explained in the
Javadoc)

# Retrieve all Users

If you want to retrieve all users you can call the following method:

```java
OsiamConnector oConnector = [Retrieving an OsiamConnector](create-osiam-connector.md)
AccessToken accessToken = [Retrieving an AccessToken](login-and-getting-an-access-token.md#retrieving-an-accesstoken)
SCIMSearchResult<User> searchResult = oConnector.getAllUsers(accessToken);
int numberOfUsers = searchResult.getTotalResults();
for (User actUser : searchResult.Resources()) {
	//...
}
//...
```

# Search for User

The [Query](query.md) class helps you
to create an Query based on the needed filter and other attributes.

A complete example how you can run a search for a user is described below:

```java
OsiamConnector oConnector = [Retrieving an OsiamConnector](create-osiam-connector.md)
AccessToken accessToken = [Retrieving an AccessToken](login-and-getting-an-access-token.md#retrieving-an-accesstoken)
Query query = [Create an Query](query.md)
SCIMSearchResult<User> result = oConnector.searchUsers(query, accessToken);
```
