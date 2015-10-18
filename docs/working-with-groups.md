To perform any actions with groups you always need an access token. For
retrieving an access token please see [Login and getting an access token]
(login-and-getting-an-access-token.md).

The group that will be used by OSIAM and the java connector is
org.osiam.resources.scim. **Group**.
Detailed information about can be found in the [scim-schema documentation]
(https://github.com/osiam/scim-schema/blob/master/docs/README.md).

Chapters:
- [Group Object](#group)
- [Creating a Group](#create-a-group)
- [Deleting a Group](#delete-a-group)
- [Update a Group](#update-a-group)
- [Retrieve a single Group](#retrieve-a-single-group)
- [Retrieve all Groups](#retrieve-all-groups)
- [Search for groups](#search-for-groups)
 - [by search string](#search-for-groups-by-search-string)
 - [by Query](#search-for-groups-by-query)
      - [braces](#braces)

# Group
The OSIAM Connector4Java is working with org.osiam.resources.scim. **Group**
Objects.

A new group can be created with his builder:

```java
Group group = new Group.Builder(<DisplayName>)
                //...
                .build();
```

Some variables of a group are:

```java
String displayName = group.getDisplayName();
Set<MultiValuedAttribute> members = group.getMembers();
//...
```

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

The OSIAM connector4Java provides an easy an quick way to change group values or group members.
It is easy to change data, even if, you want to add one user to all current groups.

The following examples show how you can use the update methods:

```java
OsiamConnector oConnector = [Retrieving an OsiamConnector](create-osiam-connector.md)
AccessToken accessToken = [Retrieving an AccessToken](login-and-getting-an-access-token.md#retrieving-an-accesstoken)
```

If you want to update a group you need to know its ID (UUID). Get the ID with:

    Group existingGroup = [get existing Group](#retrieve-a-single-group)

Now you can create a UpdateGroup and change the attributes:

    UpdateGroup updateGroup = [creating an UpdateGroup](https://github.com/osiam/scim-schema/blob/master/docs/api/update-group.md);

After this you just have to call the UpdateGroup method which will return the
complete updated group:

    Group updatedGroup = oConnector.updateGroup(existingGroup.getId(), updateGroup, accessToken);

If you want to change the same attributes for several groups you can call the
method several times with different group ID's and the same UpdateGroup Object:


```java
Group updatedGroup01 = oConnector.updateGroup(groupId01, updateGroup, accessToken);
Group updatedGroup02 = oConnector.updateGroup(groupId02, updateGroup, accessToken);
Group updatedGroup03 = oConnector.updateGroup(groupId03, updateGroup, accessToken);
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
