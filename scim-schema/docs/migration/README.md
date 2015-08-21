# Migration

## from release 0.42 to 1.0

* Renamed all setMultiValue-Methods to addMultiValue

Example:

```
addEmails(Collection<Email> emails)

addEmail(Email email)
```

instead of:

`setEmails(List<Email> emails)`

## from release 0.41 to 0.42

* Creating an extension follows the builder pattern now. You can create an extension now the following way:

 `Extension extension = new Extension.Builder("urn").setField("fieldName", "value").build();`

 * The method names in the UpdateUser class from Im and Address has been changed to singular. Please refactor you code e.g. addIms(...) to addIm(...)

## from release 0.40 to 0.41
* No migration needed

## from release 0.39 to 0.40
* The Photo object takes an URI and an ImageDataURI and not an string anymore.

## from release 0.38 to 0.39
* Deprecated `getAllExtensions()` in `User` class, please use getExtensions
* Deprecated `getAllFields()` in `Extension` class, please use getFields

## from release 0.37 to 0.38
* Add UpdateUser and UpdateGroup
* User/Group: Change from setSchema(Set<String> schemas) to addSchema(String schema)

## from release 0.36 to 0.37
* All MultiValuedAttributes (Email, Role, Address, ...) extends the abstract class MultiValuedAttribute
* All Lists of the User are the concrete Type e.g. ```List<MultiValuedAttribute>``` -> ```List<Email>```
* The field displayName of the Group could now only be set by the constructor: Group.Builder().setDisplayName(displayName) -> Group.Builder(displayName)
* Role and X509Certificate now have also a type field
