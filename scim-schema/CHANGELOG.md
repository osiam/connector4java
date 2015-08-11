# OSIAM SCIM Schema

## Unreleased

### Fixes

- Refactor UserDeserializer to significantly improve performance

### Changes

- Remove unused meta classes and package
- Deprecate User/Group/Resource#setSchemas(...)

## 1.4 - 2015-06-17

### Fixes

- Email validation does not allow upper-cased TLD

### Changes

- Improve email address validation
- Bump dependencies
- Rename `RELEASE.NOTES` to `CHANGELOG.md`

## 1.3.2 - 2014-11-24
- release because of fixes in addon-administration

## 1.3.1 - 2014-10-27
- release because of fixes in addon-self-administration

## 1.3 - 2014-10-17
- removed JSONAssert due to licensing issues (https://github.com/skyscreamer/JSONassert/issues/44)

## 1.2 - 2014-09-30
- release because of fixes in addon-self-administration

## 1.1 - 2014-09-17
- mostly dependency version updates
- removed invalid field 'empty' in JSON output, which is not defined by SCIM

## 1.0 - 2014-05-15
Finally released version 1.0!
- [refactore] Renamed all setMultiValue-Methods to addMultiValues, add the method addMultiValue,
  deleteMultiValue, clearMultiValues -> MultiValue: Email, PhoneNumber, ...
  Migration: https://github.com/osiam/scim-schema/wiki/Migration#from-release-042-to-10

## 0.42 - 2014-04-30
- [refactore] Extension have been refactored to the builder pattern
- [rafactore] ScimHelper renamed into SCIMHelper
- [refactore] correct naming of methods in UpdateUser

## 0.41 - 2014-04-02
- [feature] Extension fields could also get by getValueAs[Type] e.g. getValueAsString
- [feature] Added ScimHelper for static helper functions. Started with a function to extract
  email from user.
- [feature] Added new constructor to Builder of User and Group.
  For Group: Builder(String displayName, Group group) -> easy copy of Group with new displayName
  For User: Builder(String userName, User user) -> easy copy of User with new userName

## 0.40 - 2014-03-17
- [feature] The Photo object can take and return a URI and a ImageDataURI as value. The getter and setter for the
  string value have been removed. The ImageDataURI can also take an image InputStream as value.

## 0.39 - 2014-03-04
- [feature] add methods User.getExtensions() and Extension.getFields() and set methods User.getAllExtensions() and
  Extension.getAllFields() to @Deprecated

## 0.38 - 2014-02-14
- [feature] OSNG-215 - Develop PATCH "Delete" support on extensions
  For migration see: https://github.com/osiam/scim-schema/wiki/Migration#wiki-from-release-037-to-038

