To update an Group you need to create an specific Group. For more details how an update Group has to look please see at the [SCIM specification](http://www.simplecloud.info/).

The classs UpdateGroup was created to make this process more easy and secure.

You can create an UpdateGroup with his builder like:

```
UpdateGroup updateGroup = new UpdateGroup.Builder()
                 //...
                .build();
```

If you are using the [connector4java](https://github.com/osiam/connector4java) you can directly use this UpdateGroup object.

[osiamConnector.updateGroup(group_id, updateGroup, accessToken);](https://github.com/osiam/connector4java/wiki/Working-with-groups#update-a-group)

In case you want to use the scim group directly you can call

```
Group group = updateUser.getScimConformUpdateGroup();
```

## Single Attributes

The following actions can be done with single attributes

### update an single attribute

```
updateGroupBuilder.updateDisplayName("newDisplayName")
        .updateExternalId("newExternalId");
```

### delete an single attribute

```
updateGroupBuilder.deleteExternalId();
```

The main attribute displayName can't be deleted.

## Members

The following actions can be done with Members

### add a Member

```
updateGroupBuilder.addMember(user.getId());
updateGroupBuilder.addMember(group.getId());
```

### delete all Members

```
updateGroupBuilder.deleteMembers()
```

### delete a single Member

```
updateGroupBuilder.deleteMember(user.getId());
updateGroupBuilder.deleteMember(group.getId());
```