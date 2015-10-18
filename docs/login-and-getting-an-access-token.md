# Login

For general information about the different OAuth 2.0 grant types please see
the [Overview of Osiam]
(https://github.com/osiam/osiam/blob/master/docs/OSIAM-Overview.md#oauth-20).
A more technical explanation can be found in the [API documentation]
(https://github.com/osiam/osiam/blob/master/docs/api_documentation.md#oauth2).

Table of contents
- [Retrieving an access token](login-and-getting-an-access-token.md#retrieving-an-access-token)
   - [Authorization Code Grant](login-and-getting-an-access-token.md#authorization-code-grant)
   - [Resource Owner Password Credentials Grant](login-and-getting-an-access-token.md#resource-owner-password-credentials-grant)
   - [Client Credentials Grant](login-and-getting-an-access-token.md#client-credentials-grant)
- [Access token overview](login-and-getting-an-access-token.md#access-token-overview)

# Scopes

You access token will have some scopes to grant access to the different
procedures.

The supported scopes in OSIAM are listed [here](https://github.com/osiam/osiam/blob/master/docs/api_documentation
.md#supported-scopes)

With **new Scope("your scope");** you can also create and add your own scopes that you need an your application

# Retrieving an access token

In a trusted environment getting an access token from OSIAM implies a successful
login of the user. 

## [Authorization Code Grant](https://github.com/osiam/osiam/blob/master/docs/api_documentation.md#authorization-code-grant)

The authorization code grant should always be used as default grant, as other
grants are less secure and should only be used if you know what you are doing.

It takes to steps to get an access token using the authorization code grant:

1. Redirect the user to OSIAM and let the user login. You will get an
   **auth code**.
2. Send the auth code to the OAuth 2.0 in exchange for the access token.

To do so follow these steps:

* Get the connector object

    OsiamConnector oConnector = [Retrieving an OsiamConnector](create-osiam-connector.md#grant-authorization-code)

* Redirect the user to OSIAM. You can get the redirect URI with the
following command:

    URI redirectURI = oConnector.getRedirectLoginUri(Scope.GET, Scope.PUT);

* After the user is successfully authenticated, OSIAM redirects the
user back to your application's redirect URI.

* If everything went fine you will receive the following parameter

    <YOUR_REDIRECT_URI>?code=<AUTHENTICATION_CODE> // For example: https://localhost/?code=KkYz8C

* Otherwise you get a different redirect with parameters containing more details about the situation

    <YOUR_REDIRECT_URI>?error=access_denied&error_description=User+denied+access

* Send the auth code to OSIAM in order to get the access token

    AccessToken accessToken = oConnector.retrieveAccessToken(<AUTHENTICATION_CODE>);	

A complete simple Example written with vert.x can be found [here]
(vertx-example.md)

If you got the token of AccessToken as simple String you also can create an
AccessToken the following way

    AccessToken accessToken = new AccessToken.Builder(<token>).build();

## [Resource Owner Password Credentials Grant](https://github.com/osiam/osiam/blob/master/docs/api_documentation.md#resource-owner-password-credentials-grant) 
and
## [Client Credentials Grant](https://github.com/osiam/osiam/blob/master/docs/api_documentation.md#client-credentials-grant)

The OSIAM Connector4java allows you to retrieve an
**org.osiam.client.oauth.accessToken**. The access token is required to access
protected resources like an user's e-mail address.
 
To retrieve an access token Object, you have to create an OsiamConnector Object
first.

    OsiamConnector oConnector = [Retrieving an OsiamConnector]
    (create-osiam-connector.md#resource-owner-password-credentials-grant)

Now it is possible to retrieve a valid access token.

In case you want an AccessToken for an '**Client Credentials Grant**' you just
can call the method by providing the needed scopes.

    AccessToken accessToken = oConnector.retrieveAccessToken(Scope.GET, Scope.PUT, ...);

In case you want an AccessToken for an '**Resource Owner Password Credentials
Grant**' you also have to provide the userName and the userPassword

    AccessToken accessToken = oConnector.retrieveAccessToken(<userName>, <userPassword>, Scope.GET, Scope.PUT, ...);

# Refresh of an AccessToken

Depending of the client configuration it is possible to refresh an AccessToken
after it is expired. You need an can refresh an AccessToken if the following is
true:

    if(accessToken.isExpired() == true && accessToken.isRefreshTokenExpired() == false) 

in this case you can refresh the AccessToken with the OsiamConnector

    OsiamConnector oConnector = [Retrieving an OsiamConnector]
    (create-osiam-connector.md#resource-owner-password-credentials-grant)

# Access token overview

Some of the main methods in an Access Token are:

```java
accessToken.isExpired(); // true if the accessToken is expired
accessToken.getRefreshToken(); // gets a token that you will need if you want to refresh the token
accessToken.isRefreshTokenExpired(); // true if the refresh token in the AccessToken is expired and the user needs to login again
accessToken.getScopes(); //A Set of all scope of the token 
accessToken.isClientOnly(); // true if the AccessToke was created in a "Client Credential flow" and no User belongs to it
accessToken.getUserId(); // returns the id of the user the AccessToken belongs to
accessToken.getUserName(); // returns the userName of the user the AccessToken belongs to 
```
