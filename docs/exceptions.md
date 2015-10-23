The OSIAM Connector4Java provides different own exceptions to provide a easy
way to handle the different reasons of failure.

Each Exception contains a individual code and a Exception message.

The exceptions are:

- [OsiamClientException](exceptions.md#osiamclientexception)
  - [InvalidAttributeException](exceptions.md#invalidattributeexception)
  - [ConnectionInitializationException](exceptions.md#connectioninitializationexception)
  - [OsiamRequestException] (exceptions.md#osiamrequestexception)
     - [ConflictException] (exceptions.md#conflictexception)
     - [ForbiddenException] (exceptions.md#forbiddenexception)
     - [NoResultException] (exceptions.md#noresultexception)
     - [UnauthorizedException] (exceptions.md#unauthorizedexception)

- [Individual error messages and codes](exceptions.md#error-messages-and-codes)

## OsiamClientException

OsiamClientException is the base exception and extends RuntimeException.

### InvalidAttributeException

The InvalidAttributeException extends the OsiamClientException and will be
thrown if an invalid argument has been given (e.g. an AccessToken that is null).

### ConnectionInitializationException

The ConnectionInitializationException extends the OsiamClientException and will
be thrown if no connection to OSIAM could be created.

### OsiamRequestException

The OsiamRequestException extends the OsiamClientException. It is the base
exception of all exceptions that indicate that the connection to OSIAM was
successful but something else went wrong. This and all child exceptions will
also provide a method to retrieve the HTTP status code which raised the
exception. 

    int getHttpStatusCode()

This exception will be raised if an unknown error is returned by OSIAM. Known
errors are represented by child exceptions described below.

#### ConflictException

The ConflictException extends the OsiamRequestException and will be thrown if
any conflicts happened while processing the request by OSIAM (HTTP status code
409).

#### ForbiddenException

The ForbiddenException extends the OsiamRequestException and will be thrown if
you are not allowed to do the requested task (HTTP status code 403).

#### NoResultException

The NoResultException extends the OsiamRequestException and will be thrown if a
User/Group which wants to be created or deleted do not exist (HTTP status code
404).

#### UnauthorizedException

The UnauthorizedException extends OsiamRequestException and will be thrown if
you are not authorized to do the requested task (HTTP status code 401).
