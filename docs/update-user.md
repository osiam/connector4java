To update an User you need to create an specific User. For more details how an update User has to look please see at the [SCIM specification](http://www.simplecloud.info/).

The classs UpdateUser was created to make this process more easy and secure.

You can create an UpdateUser with his builder like:

```
UpdateUser updateUser = new UpdateUser.Builder()
                 //...
                .build();
```

If you are using the [connector4java](https://github.com/osiam/connector4java) you can directly use this UpdateUser object.

[osiamConnector.updateUser(user_id, updateUser, accessToken);](https://github.com/osiam/connector4java/wiki/Working-with-user#update-a-user)

In case you want to use the scim user directly you can call

```
User user = updateUser.getScimConformUpdateUser();
```

## Single Attributes

The following actions can be done with single attributes

### update an single attribute

```
updateUserBuilder.updateActive(true)
         .updateNickName("new Nickname")
         .updateUserName("newUserName")
         .updatePassword("newPassword");
```

### delete an single attribute

```
updateUserBuilder.deleteNickName()
          .deleteTitle();
```

Main attribute like userName, active or the password can't be deleted.

## MultiValuedAttribtutes

The following actions can be done with MultiValueAttribtues

### add a new MultiValuedAttribute

```
Email email = getNewEmail();
Address address = getNewAddress();

updateUserBuilder.addEmail(email)
        .addAddress(address);
```

### delete all MultiValuedAttribute of one Type

```
updateUserBuilder.deleteEmails()
        .deleteAddresses();
```

### delete a single MultiValuedAttribute

```
Email email = getDeleteEmail();
Address address = getDeleteAddress();

updateUserBuilder.deleteEmail(email)
         .deleteAddress(address);
```

### update an MultiValuedAttribute

```
Email oldEmail = getOldEmail();
Email newEmail = getNewEmail();
Address oldAddress = getOldAddress();
Address newAddress = getNEwAddress();

updateUserBuilder.updateEmail(oldEmail, newEmail)
         .updateAddress(oldAddress, newAddress);
```

## Extensions

The following actions can be done with Extension

### delete all Fields of an Extension

```
updateUserBuilder.deleteExtension(<urn>)
```

### delete one single Field of an extension

```
updateUserBuilder.deleteExtensionField(<urn>, <fieldName>)
```

### update extension fields

```
Extension extension = new Extension.Builder("urn")
        .setField("onFieldOfMany", "newValue")
        .build();
        
updateUserBuilder.updateExtension(extension);
```