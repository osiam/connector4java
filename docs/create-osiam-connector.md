To be able to log in, and create or change users or groups you need to create
an `org.osiam.client.connector.OsiamConnector` instance. You can do this by
using the `OsiamConnector.Builder()` class.

```java
OsiamConnector osiamConnector = new OsiamConnector.Builder()
       .setEndpoint(OSIAM_ENDPOINT)
       .setClientId(CLIENT_ID)
       .setClientSecret(CLIENT_SECRET)
       .build();
```

## Timeouts

Starting with version 1.4 you can also set the connect and read timeouts
like this:

```java
OsiamConnector.setConnectTimeout(2500);
OsiamConnector.setReadTimeout(5000);
```

As you might noticed, these settings are application-global, so you can only
define them for **all** connectors you create. This will be addressed in a
future version, but note that it is recommended to use only **one** connector
instance for the whole application, unless you need to use different OAuth
clients.

## Legacy Schemas

Starting with version 1.8 you can configure the use of legacy schemas, i.e.
schemas that were defined before SCIM 2 draft 09, like this:

```java
OsiamConnector osiamConnector = new OsiamConnector.Builder()
       ...
       .withLegacySchemas(true)
       ...
       .build();
```

This enables compatibility with OSIAM releases up to version 2.3 (resource-server 2.2). This
behavior is not enabled by default. Set it to `true` if you connect to an OSIAM version <= 2.3 and,
please, update to 2.5 or later immediately.
