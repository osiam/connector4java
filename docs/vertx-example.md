This is a simple example how to use the osiam connector4java to retrieve an
AccessToken using the oauth2 login flow. The sources can be found [here]
(https://github.com/osiam/examples/tree/master/vertx-3-legged-flow).

```java
import java.io.IOException;
import java.net.URI;

import org.osiam.client.connector.OsiamConnector;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.GrantType;
import org.osiam.client.oauth.Scope;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.http.HttpServerRequest;

public class Main {

    public static void main(String[] args) throws IOException {
        Vertx vertx = VertxFactory.newVertx();
        vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {

            OsiamConnector oConnector;

            {
                oConnector = new OsiamConnector.Builder()
                .setEndpoint("http://localhost:8180/osiam-server")
                .setClientId("example-client")
                .setClientSecret("secret")
                .setClientRedirectUri("http://localhost:5000/oauth2")
                .build();
            }

            public void handle(HttpServerRequest req) {
                String path = req.path();
                if(path.equals("/login")){
                    URI uri = oConnector.getRedirectLoginUri();
                    req.response().setStatusCode(301).putHeader("Location", uri.toString()).end();
                }if(path.equals("/oauth2")){
                    MultiMap multiMap = req.params();
                    if(multiMap.get("error") != null){
                        req.response().setChunked(true).write("The User has denied your request").end();
                    }else{
                        //the User has granted your rights to his ressources
                        String code = multiMap.get("code");
                        AccessToken accessToken = oConnector.retrieveAccessToken(code, Scope.GET, Scope.PUT, Scope.PATCH);  
                        req.response().setChunked(true).write("My access token is: " + accessToken.toString()).end();
                    }
                }
                else{
                     String file = path.equals("/") ? "index.html" : path;
                     req.response().sendFile("webroot/" + file);    
                }               
            }
        }).listen(5000);

        System.in.read();
    }
}   	
```

The needed simple html page is

```sh
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Insert title here</title>
	</head>
	<body>
		Hello, this is a very simple example how to retrieve a OSIAM access Token using the OSIAM oauth2 login flow
		<br>
		<a href="/login">login</a>
	</body>
</html>
```
