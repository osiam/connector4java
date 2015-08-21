A org.osiam.resources.scim **User** Object holds all relevant data from a User.

At this wiki we only document the getter and setter of the single attributes.
For a explanation about the "meaning" of the single fields please have a look at the [documentation](http://tools.ietf.org/html/draft-ietf-scim-core-schema-22#section-6) of [System for Cross-domain Identity Management](http://tools.ietf.org/html/draft-ietf-scim-core-schema-22)

# Table of Contents

* [Getting and Setting of Attributes](#getting-and-setting-of-attributes)
* [Attribute Types](#attribute-types)
* [Attributes](#attributes)
  * [active](#active)
  * [addresses](#addresses)
  * [displayName](#displayname)
  * [emails](#emails)
  * [entitlements](#entitlements)
  * [externalId](#externalid)
  * [groups](#groups)
  * [id](#id)
  * [ims](#ims)
  * [locale](#locale)
  * [meta](#meta)
  * [name](#name)
  * [nickName](#nickname)
  * [password](#password)
  * [phoneNumbers](#phonenumbers)
  * [photos](#photos)
  * [preferredLanguage](#preferredlanguage)
  * [profileUrl](#profileurl)
  * [roles](#roles)
  * [timezone](#timezone)
  * [title](#title)
  * [userName](#username)
  * [userType](#usertype)
  * [x509Certificates](#x509certificates)
  * [Extensions](#extensions)

## Getting and Setting of Attributes

A User can be build by using his Builder

```
User user = new User.Builder()
                 //all need variables
                 .build();
```

To explain all getter and setter we use a 


```
User user;
```

object to explain all getter.

A 


```
User.Builder builder;
```

object is used to explain all setter.

## Attribute Types

Many multi valued attributes can have a specific type e.g. HOME, OTHER, ICQ.

The OSIAM scim schema provides the canonical in a easy way to use but also custom types are possible.

```
Email.Type canonicalType = Email.Type.WORK;
Email.Type customType = new Email.Type("custom");
```

## Attributes

### active

**GET:**
```
boolean isActive = user.isActive();
```

**SET:**
```
builder.setActive(true);
```

### addresses

(MultiValuedAttribute)

**GET:**
```
List<Address> addressList = user.getAddresses();
      Address firstAddress = addressList.get(0);
      String country = firstAddress.getCountry();
      Address.Type type = firstAddress.getType();
      //...
```

**SET:**
```
Address newAddress = new Address.Builder()
            		.setCountry("example Country")
            		.setType(Address.Type.HOME)
            		.build();
builder.addAddress(newAddress);
```

The canonical Address.Types are

> WORK, HOME, OTHER

### displayName

**GET:**
```
String displayName = user.getDisplayName();
```

**SET:**
```
builder.setDisplayName("example DisplayName");
```

### emails

(MultiValuedAttribute)

**GET:**
```
List<Email> emailList = user.getEmails();
     Email firstEmail = emailList.get(0);
     String value = firstEmail.getValue();
     Email.Type type = firstEmail.getType();
```

**SET:**
```
Email newEmail = new Email.Builder()
            .setValue("max@example.com")
            .setType(Email.Type.WORK)
            .build();
builder.addEmails(newEmail);
```

The canonical Email.Types are

> WORK, HOME, OTHER

### entitlements

(MultiValuedAttribute)

**GET:**
```
List<Entitlement> EntitlementlList = user.getEntitlements();
Entitlement firstEntitlement = EntitlementlList.get(0);
String value = firstEntitlement.getValue();
Entitlement.Type type = firstEntitlement.getType();
```

**SET:**
```
Entitlement newEntitlement = new Entitlement.Builder()
            	.setValue("your Entitlement")
            	.setType(new Entitlement.Type("your Type"))
            	.build();
builder.addEntitlement(newEntitlement);
```

The entitlement has no canonical types.

### externalId

Used to be able to match the OSIAM User with a User in a external System.

**GET:**
```
String externalId = user.getExternalId();
```

**SET:**
```
builder.setExternalId("external ID");
```

### groups

(MultiValuedAttribute)

The getGroups() method provides a List of Group ids where the actual User is a member of.
With this group id you can get the group you need from the OSIAM Server

**GET:**
```
List<GroupRef> groupRefList = user.getGroups();
GroupRef firstGroupRef = groupRefList.get(0);
String value = firstGroupRef.getValue();
UUID groupId = UUID.fromString(value);
GroupRef.Type type = firstGroupRef.getType();
```
The canonical GroupRef.Types are (The will only be set by the OSIAM server):

> DIRECT, INDIRECT

### id

The id is automatic given by the OSIAM Server at the Moment the User is created.
The id will be provided as String but is based on a UUID.

**GET:**
```
String id = user.getId();
UUID uuid = UUID.fromString(id);
```

### ims

(MultiValuedAttribute)

**GET:**
```
List<Ims> groupRefList = user.getIms();
Ims firstGroupRef = groupRefList.get(0);
String value = firstGroupRef.getValue();
Ims.Type type = firstGroupRef.getType();
```

**SET:**
```
Im newIm = new Im.Builder()
           .setValue("im address")
           .setType(Im.Type.AIM)
           .build();
builder.addIms(newIm);
```


The canonical ImsTypes are

> AIM, GTALK, ICQ, XMPP, MSN, SKYPE, QQ, YAHOO

### locale

**GET:**
```
String local = user.getLocale();
```

**SET:**
```
builder.setLocale("local");
```

### meta

**GET:**
```
Meta metaData = user.getMeta();
Date lastMod =  metaData.getLastModified();
//...
```

**SET:**
```
// the meta data is set by the OSIAM server
```

### name

**GET:**
```
Name name = user.getName();
String givenName = name.getGivenName();
```

**SET:**
```
Name newName = new Name.Builder()
            .setGivenName("givenName")
            //...
            .build();
builder.setName(newName);
```

### nickName

**GET:**
```
String nickName = user.getNickName();
```

**SET:**
```
builder.setNickName("nickName of the User");
```

### password

**GET:**
```
String nullPassword = user.getPassword(); // The password is always null 
// The OSIAM server will never provide the password from a User
```

**SET:**
```
builder.setPassword("password of the User to be created");
```

### phoneNumbers

(MultiValuedAttribute)

**GET:**
```
List<PhoneNumber> phoneNumberList = user.getPhoneNumbers();
PhoneNumber firstPhoneNumber = phoneNumberList.get(0);
String value = firstPhoneNumber.getValue();
PhoneNumber.Type type = firstPhoneNumber.getType();
```

**SET:**
```
PhoneNumber newPhoneNumber = new PhoneNumber.Builder()
            		.setValue("012345678")
            		.setType(PhoneNumber.Type.HOME)
            		.build();
builder.addPhoneNumbers(newPhoneNumber);
```

The canonical PhoneNumber.Types are

> WORK, HOME, MOBILE, FAX, PAGER, OTHER

### photos

(MultiValuedAttribute)

**GET:**
```
 List<Photo> photoList = user.getPhotos();
Photo firstPhoto = photoList.get(0);
PhotoValueType valueType = firstPhoto.getValueType();
if(valueType == PhotoValueType.URI){
  URI uri = firstPhoto.getValueAsURI();
}
if(valueType == PhotoValueType.IMAGE_DATA_URI){
  ImageDataURI imageUri = firstPhoto.getValueAsImageDataURI();
  InputStream inputStream = imageUri.getAsInputStream()
  URI uri02 = imageUri.getAsURI()
}
Photo.Type type = firstPhoto.getType();
```

**SET:**
```
URI uri = new URI("http://www.exampleMe.html/myPhoto.jpg");
Photo newPhoto = new Photo.Builder()
            .setValue(uri)
            .setType(Photo.Type.PHOTO)
            .build();
ImageDataURI imageUri = new ImageDataURI(getPictureInputStream());
Photo newPhoto02 = new Photo.Builder()
            .setValue(imageUri)
            .build();
List<Photo> photos = new ArrayList<>();
photos.add(newPhoto);
photos.add(newPhoto02);
builder.addPhotos(photos);
```

The canonical Photo.Types are

> PHOTO, THUMBNAIL

### preferredLanguage

**GET:**
```
String preferredLanguage = user.getPreferredLanguage();
```

**SET:**
```
builder.setPreferredLanguage("en");
```

### profileUrl

**GET:**
```
String profileUrl = user.getProfileUrl();
```

**SET:**
```
builder.setProfileUrl("http://www.myProfile.com");
```

### roles

(MultiValuedAttribute)

**GET:**
```
List<Role> roleList = user.getRoles();
Role firstRole = roleList.get(0);
String value = firstRole.getValue();
```

**SET:**
```
Role newRole = new Role.Builder()
            	.setValue("example Student")
            	.build();
builder.addRoles(newRole);
```

### timezone

**GET:**
```
String timezone = user.getTimezone();
```

**SET:**
```
builder.setTimezone("America/Los_Angeles");
```

### title

**GET:**
```
String title = user.getTitle();
```

**SET:**
```
builder.setTitle("Chief of Chiefs");
```

### userName

**GET:**
```
String userName = user.getUserName();
```

**SET:**
```
builder = new User.Builder("userName");
```

### userType

**GET:**
```
String userType = user.getUserType();
```

**SET:**
```
builder.setUserType("type of the User");
```

### x509Certificates

(MultiValuedAttribute)

**GET:**
```
List<X509Certificate> certifacteList = user.getX509Certificates();
X509Certificate firstCertificate = certifacteList.get(0);
String value = firstCertificate.getValue();
```

**SET:**
```
X509Certificate newX509Certificate = new X509Certificate.Builder()
            	.setValue(<ENCODED_CERTIFICATE>)
            	.build();
builder.addX509Certificate(newX509Certificate);
```

### Extensions

If the core fields from SCIM are not sufficent for your use case, you can define extended fields for a User. These collections of user defnied fields are called extensions. Extensions are simple key-value-pairs which are identified by a unique URN.

Extensions are fully supported on OSIAM Server.
If the scim schema is used together with the OSIAM Server the extension need to be [configured](https://github.com/osiam/server/wiki/detailed_reference_installation#configuring-scim-extension) before the can be used.

**Create an extension**

```
String extensionUrn = "urn:org.osiam.extensions:Test:1.0"; // unique urn of the extension
Extension.Builder extensionBuilder = new Extension.Builder(extensionUrn)
.setField("gender", "male")
.setField("size", BigDecimal.valueOf(1.78))
.setField("numberChildren", BigInteger.valueOf(2));
        
DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime().withZoneUTC();
Date birthDay = new Date(dateTimeFormatter.parseDateTime("1980-01-23T04:56:22.000Z").getMillis());
        
extensionBuilder.setField("birth", birthDay)
.setField("newsletter", true);
        
Extension extension = extensionBuilder.build();
```

**Add an extension to a user**
```
User user = new User.Builder("userName")
        .addExtension(extension)
        .build();
```

**Retrieve an extension from a user**

An extension is only returned by the OSIAM server if any field of this extension has been set. For this it is always a good idea to check if the extension is present in the User object.
 
```
String urn = "urn:org.osiam.extensions:Test:1.0";
if (user.isExtensionPresent(urn)) {
       Extension extension2 = user.getExtension(urn);
}
```

**Check presence of a field**

Even if an Extension is returned it can be that an single Field is not present while it is not set. So, also make sure to check if an extensionfield is present before getting it.

```
if(extension.isFieldPresent("newsletter")) {
    // do something with field
}
```

**Get the value of a Field**

If a field does not exist a NoSuchElementException Exception will be thrown.

```
try{
      extension.getFieldAsString("nonExistingField");
}catch(NoSuchElementException e){
      //TODO something
}
```

The exception can be avoided if you check first if the extension field is present like in the following
```
if(extension.isFieldPresent("existingField")){
     BigInteger existingField = extension.getFieldAsInteger("exisitngField");
}
```
Otherwise you can get the value of a field like this:

```
String stringValue = extension.getField("gender", ExtensionFieldType.STRING);
String stringValue02 = extension.getFieldAsString("gender");
BigDecimal decimalValue = extension.getField("size", ExtensionFieldType.DECIMAL);
BigDecimal decimalValue02 = extension.getFieldAsDecimal("size");
BigInteger integerValue = extension.getField("numberChildren", ExtensionFieldType.INTEGER);
BigInteger integerValue02 = extension.getFieldAsInteger("numberChildren");
Date dateValue = extension.getField("birth", ExtensionFieldType.DATE_TIME);
Date dateValue02 = extension.getFieldAsDate("birth");
Boolean boolValue = extension.getField("newsletter", ExtensionFieldType.BOOLEAN);
Boolean boolValue02 = extension.getFieldAsBoolean("newsletter");
URI uriValue = extension.getField("homepage", ExtensionFieldType.REFERENCE);
URI uriValue02 = extension.getFieldAsReference("homepage");
```

It is also possible to use the Extension Object to map the value to another type (if a conversion is possible)

```
String decimalValue = extension.getFieldAsString("size");
String integerValue = extension.getFieldAsString("numberChildren");
String boolValue = extension.getFieldAsString("newsletter");
String uriValue = extension.getFieldAsString("homepage");
```

**Change the value of a Field**
```
extension.setField("newsletter", false);
```
