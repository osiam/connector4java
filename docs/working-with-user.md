To perform any actions with users you always need an access token. For
retrieving an access token please see [Login and getting an access token](login-and-getting-an-access-token.md).

Chapters:
- [Create a User](#create-a-user)
- [Delete a User](#delete-a-user)
- [Update a User](#update-a-user)
- [Retrieve a single User](#retrieve-a-single-user)
- [Retrieve the currently logged in User by his access token](#retrieve-the-current-logged-in-user-by-his-access-token)
- [Retrieve all User](#retrieve-all-users)
- [Search for Users](#search-for-user)

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

The OSIAM Connector4Java provides a way to change user data.
You are able to update just a single value, all of them, or anything in between.
The following examples shows how you can update a user.

If you want to update a user you need to know their ID (UUID).
Then you can create a new user based on the original user and change their attributes:

```
User newUser = new User.Builder(originalUser)
        .setActive(true)
        // Updating an email address
        .removeEmail(originalUser.emails[0])
        .addEmail(new Email.Builder(originalUser.emails[0])
                .setValue('newValue1@example.com')
                .setPrimary(true)
                .build())
        // Adding an email address
        .addEmail(new Email.Builder()
                .setValue('newValue2@example.com')
                .setType(Email.Type.HOME)
                .build())
        // Removing an email address
        .removeEmail(originalUser.emails[1])
        .build();
```

After this you just have to call the `replaceUser` method that will return the updated user:

```
User updatedUser = oConnector.replaceUser(originalUser.getId(), newUser, accessToken);
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
