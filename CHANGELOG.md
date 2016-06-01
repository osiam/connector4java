# OSIAM Connector4Java

## Unreleased

### Features

- Introduce a `getMe()` method to retrieve the currently logged in user
  from OSIAM 3.0

### Changes

- The user name can now be set via the builder

### Fixes

- Resources could be changed by using the copy-of constructor of the builders, though they were supposed
  to be immutable.
- A `400 BAD REQUEST` response now creates a `BadRequestException` instead of a `ConflictException` 
- Return the right builder type in `Photo.Builder#setValue(String)`.

### Deprecations

- The `BasicUser` class and the method `OsiamConnector.getCurrentUserBasic()`
- `Group.Builder#setId(String)` and `User.Builder#setId(String)`
- `Group.Builder#setMeta(Meta)` and `User.Builder#setMeta(Meta)`
- `User.Builder.Builder(String, User)`

## 1.8 - 2015-12-12

### Features

- Set network timeouts on a per connector basis

    Related methods:
    - `OsiamConnector.Builder#withReadTimeout`
    - `OsiamConnector.Builder#withConnectTimeout`

- Manage OAuth 2 clients via the connector

    Related methods:
    - `AuthService#getClients`
    - `AuthService#getClient`
    - `AuthService#createClient`
    - `AuthService#updateClient`
    - `AuthService#deleteClient`

- Support legacy SCIM schemas for connecting to OSIAM <= 2.3

    See [Create an OSIAM connector](docs/create-osiam-connector.md#legacy-schemas), if you use
    OSIAM <= 2.3.

- Support OSIAM 3.x

    See [Create an OSIAM connector](docs/create-osiam-connector.md#osiam-3x), if you use OSIAM 3.0.

### Changes

- `OsiamConnector#setMaxConnections` also sets the maximum connections per route

    Anticipate that auth-server and resource-server are installed under the same domain.
    Therefore, maximum number of connections is the same as maximum connections per route.
    Additionally use `OsiamConnector#setMaxConnectionsPerRoute`, if your auth-server and
    resource-server are not installed under the same domain.

- Support Jackson versions < 2.5 in classpath

    Applications that use Jackson < 2.5 can use the connector without upgrading Jackson.
    See [#168](https://github.com/osiam/connector4java/pull/168) for more details.

- Throw `BadCredentialsException` when retrieving access token with wrong client credentials

    See [#173](https://github.com/osiam/connector4java/pull/173) for more details.

- Throw `BadRequestException` when auth-server responds with `400 Bad Request`

    See [#173](https://github.com/osiam/connector4java/pull/173) for more details.

### Fixes

- Missing `Content-Length` header on some requests

    See also: [#179](https://github.com/osiam/connector4java/issues/179)
    and [#180](https://github.com/osiam/connector4java/pull/180)

### Other

- Migrate documentation from wiki to markdown files in repo

    You can find the documentation in the [`docs/`](docs/) folder.

### Updates

- `scim-schema` 1.6

    See [`scim-schema`'s changelog](https://github.com/osiam/scim-schema/blob/v1.6/CHANGELOG.md)
    for more details

- `joda-time` 2.8.2
- Jersey 2.22

## 1.7 - 2015-09-11

### Changes

- Update scim-schema to solve performance issues when deserializing `User`s
- Deprecate old, method-based scopes (GET, POST, PUT, PATCH, DELETE)
- Adjust the client to the SCIM compliant errors (Compatibility with OSIAM <= 2.x is provided)
- Throws `IllegalStateException` when using a non-configured service endpoint
  (was `InvalidAttributeException`)

## 1.6 - 2015-06-17

### Features

- Allow configuration of maximum number of HTTP connections and connections
  per route

    Related methods:

    * `OsiamConnector#setMaxConnections(int)`
    * `OsiamConnector#setMaxConnectionsPerRoute(int)`

### Changes

- Bump scim-schema to version 1.4

## 1.5.1 - 2015-06-01

### Fixes

- IllegalArgumentException from Jackson when searching for resources

## 1.5 - 2015-06-01

### Features

- add ADMIN scope
- add ME scope
- handle HTTP Forbidden from auth-server

### Changes

- bump dependencies

## 1.4 - 2015-02-25
- [feature] allow configuration of connect and read timeout

## 1.3.2 - 2014-11-24
- release because of fixes in addon-administration

## 1.3.1 - 2014-10-27
- [fix] fixed creation of auth server by retrieving refresh token

## 1.3 - 2014-10-17
- nothing changed

## 1.2 - 2014-09-30
- [feature] shading all 3rd-party dependencies
  3rd-party dependencies are shaded and packages relocated to eliminate
  possible dependency and jar hell issues. this enables the usage with
  Dropwizard and other projects depending on a different version of JAX-RS
  or Jersey. Note: Because of this you cannot rely on transitive dependencies
  which may have been introduced by the connector to your project. If your
  build goes red after updating to 1.2, just fix your dependencies.

## 1.1 - 2014-09-17
- [feature] revocation of access tokens
  It is now possible to revoke access tokens by using
  OsiamConnector#revokeAccessToken(AccessToken tokenToRevoke) to revoke a
  single access token and
  OsiamConnector#revokeAllAccessTokens(String id, AccessToken accessToken) to
  revoke all access tokens of a given user (the access token supplied to this
  method is for used for authentication).
- [enhancement] updated dependencies: Jersey 2.12, Joda Time 2.4
- [fix] memory leak caused by Jersey client
  Jersey is leaking cloned instaces of
  org.glassfish.jersey.client.ClientConfig.State on each request. see
  https://java.net/jira/browse/JERSEY-2378 for further details.
- [fix] OAuth error message will not be consumed
  In case of an error with OAuth the resulting error message was not consumed
  by the connector, only SCIM errors were.
- [fix] many typos in README.md

## 1.0 - 2014-05-15
Finally released version 1.0 of all important OSIAM projects!
- [enhancement] You can check if an access token is valid
- [enhancement] AccessToken provides additional information about the user it belongs to and the
  expiration date of the refresh token. The AccessToken can be created with a Builder.
- [refactor] The OsiamConnector.Builder doesn't need all information to create an AccessToken. You can set
  the needed information directly in the retrieveAccessToken(...) methods. The OsiamConnector provides one
  retrieveAccessToken-method for each grant
  For detailed information, please checkout the [migration](migration.md#from-015-to-10)

## 0.15 - 2014-04-30
- [feature] added class StringQueryBuilder
- [feature] added class QueryHelper
- [refactor] renaming some methods in the OsiamConnector.Builder
  For detailed information, please checkout the [migration](migration.md#from-014-to-015)

## 0.14 - 2014-04-02
- [enhancement] Raised SCIM-Schema version to 0.41. No migration necessary.
  -> Consider the deprecated API's for getAllExtensions and getAllFields in SCIM-Schema
- [refactoring] OsiamConnector and AccessToken can be extended now,
  because they are not final anymore and the token field in the AccessToken class is protected

## 0.13 - 2014-02-14
- [feature] OSNG-215 - Develop PATCH "Delete" support on extensions
  For migration see: [migration](migration.md#from-012-to-013)
- Updating a user is only possible by using an UpdateUser, updating with User is not supported anymore
  [migration](migration.md#from-012-to-013)
