To perform any actions with groups you always need an access token. For
retrieving an access token please see [Login and getting an access token]
(login-and-getting-an-access-token.md).

Chapters:
- [Creating a Group](#create-a-group)
- [Deleting a Group](#delete-a-group)
- [Update a Group](#update-a-group)
- [Retrieve a single Group](#retrieve-a-single-group)
- [Retrieve all Groups](#retrieve-all-groups)
- [Search for groups](#search-for-groups)
 - [by search string](#search-for-groups-by-search-string)
 - [by Query](#search-for-groups-by-query)
      - [braces](#braces)

## Create a Group

After you have created a new group you can save it with:

``java
OsiamConnector oConnector = [Retrieving an OsiamConnector](create-osiam-connector.md)

AccessToken accessToken = [Retrieving an AccessToken](login-and-getting-an-access-token.md#retrieving-an-accesstoken)

Group newGroup = [create a new Group](#group)

newGroup = oConnector.createGroup(newGroup, accessToken);
```

Now the returned Group Object also contains the ID and meta data of the newly
created group.

## Delete a Group

To delete a group call:

```java
OsiamConnector oConnector = [Retrieving an OsiamConnector](create-osiam-connector.md)
AccessToken accessToken = [Retrieving an AccessToken](login-and-getting-an-access-token.md#retrieving-an-accesstoken)
oConnector.deleteGroup(<GROUP_UUID>, accessToken);
```

## Update a Group

The OSIAM connector4Java provides a way to change group data or group members.
The following examples shows how you can update a group.

If you want to update a group you need to know its ID (UUID).
Then you can create a new group based on the original group and change its attributes:

```
Group newGroup = new Group.Builder(originalGroup)
        // Updating
        .removeMember(originalGroup.members[0])
        .addMember(new MemberRef.Builder(originalGroup.members[0])
                .setValue('d869f486-29cc-11e6-a76a-e82aea766790')
                .build())
        // Adding
        .addMember(new MemberRef.Builder()
                .setValue('e0f07f9e-29cc-11e6-b251-e82aea766790')
                .build())
        // Removing
        .removeMember(originalGroup.members[1])
        .build();
```

After this you just have to call the `replaceGroup` method that will return the updated group:

```
Group updatedGroup = oConnector.replaceGroup(originalGroup.getId(), newGroup, accessToken);
```

## Retrieve a single Group
   
To retrieve a single Group you need her UUID (for example
94bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4):

```java
OsiamConnector oConnector = [Retrieving an OsiamConnector](create-osiam-connector.md)
AccessToken accessToken = [Retrieving an AccessToken](login-and-getting-an-access-token.md#retrieving-an-accesstoken)
Group group = oConnector.getGroup(<GROUP_UUID>, accessToken);
```
(please consider the possible runtimeException which are explained in the
Javadoc)

## Retrieve all Groups

If you want to retrieve all groups you can call the following method:

```java
OsiamConnector oConnector = [Retrieving an OsiamConnector](create-osiam-connector.md)
AccessToken accessToken = [Retrieving an AccessToken](login-and-getting-an-access-token.md#retrieving-an-accesstoken)
SCIMSearchResult<Group> searchResult = oConnector.getAllGroups(accessToken);
int numberOfGroups = searchResult.getTotalResults();
for (Group actGroup : searchResult.Resources()) {
	//...
}
//...
```

## Search for groups

The [Query](query.md) class helps you
to create an Query based on the needed filter and other attributes. (The
examples in the page are for users. Please adapt them for groups)

A complete example how you can run a search for a user is described below:

```java
OsiamConnector oConnector = [Retrieving an OsiamConnector](create-osiam-connector.md)
AccessToken accessToken = [Retrieving an AccessToken](login-and-getting-an-access-token.md#retrieving-an-accesstoken)
Query query = [Create an Query](query.md)
SCIMSearchResult<Group> result = oConnector.searchGroups(query, accessToken);
```
