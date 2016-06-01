A org.osiam.resources.scim **Group** Object holds all relevant data from a Group.

At this wiki we only document the getter and setter of the single attributes.
For a explanation about the "meaning" of the single fields please have a look at the [documentation](http://tools.ietf.org/html/draft-ietf-scim-core-schema-22#section-8) of [System for Cross-domain Identity Management](http://tools.ietf.org/html/draft-ietf-scim-core-schema-22)

# Table of Contents

* [Getting and Setting of Attributes](#getting-and-setting-of-attributes)

## Attributes

* [displayName](#displayname)
* [externalId](#externalid)
* [id](#id)
* [members](#members)
* [meta](#meta)


### Getting and Setting of Attributes

A Group can be build by using his Builder

```
Group group = new Group.Builder()
                 //all need variables
                 .build();
```

To explain all getter and setter we use a 


```
Group group;
```

object to explain all getter

A 


```
Group.Builder builder;
```

object is used to explain all setter.

### displayName

**GET:**
```
String displayName = group.getDisplayName();
```

**SET:**
```
builder.displayName("display name");
```

### externalId

Used to be able to match the OSIAM Group with a Group in a external System.

**GET:**
```
String externalId = group.getExternalId();
```

**SET:**
```
builder.setExternalId("external ID");
```

### id

The id is automatic given by the OSIAM Server at the Moment the Group is created.
The id will be provided as String but is based on a UUID.

**GET:**
```
String id = group.getId();
UUID uuid = UUID.fromString(id);
```

### members

(MultiValuedAttribute)

The getMembers() method provides a List of member id's. These member can be Users or Groups.
With this id you can get the user or the group you need from the OSIAM Server

**GET:**
```
List<Member> groupMembers = group.getMembers();
Member firstMember = groupMembers.get(0);
String value = firstMember.getValue();
UUID id = UUID.fromString(value);
Member.Type type = firstMember.getType();
```

**SET:**
```
Member newMember = new Member.Builder()
            .setValue(<uuid_from_a_group_or_a_user>)
            .build();
List<Member> members = new ArrayList<>();
groups.add(newMember);
builder.setMembers(members);
```

The possible MemberType`s are

> USER, GROUP

### meta

**GET:**
```
Meta metaData = group.getMeta();
Date lastMod =  metaData.getLastModified();
//...
```

**SET:**
```
// the meta data is set by the OSIAM server
```
