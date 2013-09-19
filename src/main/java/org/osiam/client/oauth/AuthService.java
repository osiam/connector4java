package org.osiam.client.oauth;
/*
 * for licensing see the file license.txt.
 */

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;
import org.osiam.client.exception.ConflictException;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.ForbiddenException;
import org.osiam.client.exception.InvalidAttributeException;
import org.osiam.client.exception.OsiamErrorMessage;
import org.osiam.client.exception.UnauthorizedException;

/**
 * The AuthService provides access to the OAuth2 service used to authorize requests. Please use the
 * {@link AuthService.Builder} to construct one.
 */
public final class AuthService { // NOSONAR - Builder constructs instances of this class

    private static final Charset CHARSET = Charset.forName("UTF-8");
    private HttpPost post;
    private final String endpoint;
    private Header[] headers;
    private String clientId;
    private String clientSecret;
    private String clientRedirectUri;
    private String scopes;
    private String password;
    private String userName;
    private GrantType grantType;
    private HttpEntity body;

    private AuthService(Builder builder) {
    	endpoint = builder.endpoint;
    	scopes = builder.scopes;
        grantType = builder.grantType;
        userName = builder.userName;
        password = builder.password;
        clientId = builder.clientId;
        clientSecret = builder.clientSecret;
        clientRedirectUri = builder.clientRedirectUri;
    }

    private HttpResponse performRequest() {
    	if(post == null){// NOSONAR - false-positive from clover; if-expression is correct
	    	buildHead();
	    	buildBody();
	    	post = new HttpPost(getFinalEndpoint());
	        post.setHeaders(headers);
	        post.setEntity(body);
    	}
        HttpClient defaultHttpClient = new DefaultHttpClient();
        final HttpResponse response;
        try {
            response = defaultHttpClient.execute(post);
        } catch (IOException e) {
            throw new ConnectionInitializationException("Unable to perform Request ", e);
        }
        return response;
    }

    private void buildHead() {
        String authHeaderValue = "Basic " + encodeClientCredentials(clientId, clientSecret);
        Header authHeader = new BasicHeader("Authorization", authHeaderValue);
        headers = new Header[]{
        		authHeader
        	};
    }
        
    private String encodeClientCredentials(String clientId, String clientSecret) {
        String clientCredentials = clientId + ":" + clientSecret;
        return new String(Base64.encodeBase64(clientCredentials.getBytes(CHARSET)), CHARSET);
    }
    
    private void buildBody() {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("scope", scopes));
        nameValuePairs.add(new BasicNameValuePair("grant_type", grantType.getUrlParam())); // NOSONAR - we check before that the grantType is not null
        if(userName != null){// NOSONAR - false-positive from clover; if-expression is correct
        	nameValuePairs.add(new BasicNameValuePair("username", userName));
        }
        if(password != null){// NOSONAR - false-positive from clover; if-expression is correct
        	nameValuePairs.add(new BasicNameValuePair("password", password));
        }
        try {
            body = new UrlEncodedFormEntity(nameValuePairs);
        } catch (UnsupportedEncodingException e) {
            throw new ConnectionInitializationException("Unable to Build Request in this encoding.", e);
        }
    }
    
    /**
     * Provide an {@link AccessToken} for the given parameters of this service.
     *
     * @return a valid AccessToken
     * @throws ConnectionInitializationException
     *                               If the Service is unable to connect to the configured OAuth2 service.
     * @throws UnauthorizedException If the configured credentials for this service are not permitted
     *                               to retrieve an {@link AccessToken}
     */
    public AccessToken retrieveAccessToken() {
    	if(grantType == GrantType.AUTHORIZATION_CODE){// NOSONAR - false-positive from clover; if-expression is correct
    		throw new IllegalAccessError("For the grant type " + GrantType.AUTHORIZATION_CODE
    				+ " you need to retrieve a authentication code first.");
    	}
        HttpResponse response = performRequest();
        int status = response.getStatusLine().getStatusCode();

        if (status != SC_OK) {  // NOSONAR - false-positive from clover; if-expression is correct
        	String errorMessage;
        	String defaultMessage;
            switch (status) {
                case SC_BAD_REQUEST:
                	defaultMessage = "Unable to create Connection. Please make sure that you have the correct grants.";
                	errorMessage = getErrorMessage(response, defaultMessage);
                    throw new ConnectionInitializationException(errorMessage);
                case SC_UNAUTHORIZED:
                	defaultMessage = "You are not authorized to directly retrieve a access token.";
                    errorMessage = getErrorMessage(response, defaultMessage);
                    throw new UnauthorizedException(errorMessage);
                case SC_NOT_FOUND:
                	defaultMessage = "Unable to find the given OSIAM service (" + getFinalEndpoint() + ")";
                	errorMessage = getErrorMessage(response, defaultMessage);
                    throw new ConnectionInitializationException(errorMessage);
                default:
                	defaultMessage = String.format("Unable to setup connection (HTTP Status Code: %d)", status);
                	errorMessage = getErrorMessage(response, defaultMessage);
                    throw new ConnectionInitializationException(errorMessage);
            }
        }

        return getAccessToken(response);
    }

    private String getErrorMessage(HttpResponse httpResponse, String defaultErrorMessage) {
    	InputStream content = null;
        String errorMessage;
        try{
        	content = httpResponse.getEntity().getContent();
        	ObjectMapper mapper = new ObjectMapper();
            OsiamErrorMessage error = mapper.readValue(content, OsiamErrorMessage.class);
            errorMessage = error.getDescription();
        } catch (Exception e){    // NOSONAR - we catch everything
            errorMessage = defaultErrorMessage;
        }finally{
        	try{
        		if(content != null) { content.close(); }// NOSONAR - false-positive from clover; if-expression is correct
        	}catch(IOException notNeeded){/**doesn't matter**/}
        }
        if(errorMessage == null){// NOSONAR - false-positive from clover; if-expression is correct
            errorMessage = defaultErrorMessage;
        }
        return errorMessage;
    }

    /**
     * provides the needed URI which is needed to reconect the User to the OSIAM server to login.
     * A detailed example how to use this methode, can be seen in our wiki in gitHub
     * @return
     */
    public URI getRedirectLoginUri() {
    	if(grantType != GrantType.AUTHORIZATION_CODE){// NOSONAR - false-positive from clover; if-expression is correct
    		throw new IllegalAccessError("You need to use the GrantType " + GrantType.AUTHORIZATION_CODE 
    				+ " to be able to use this method.");
    	}
    	URI returnUri;
     	try {		
            returnUri =  new URIBuilder().setPath(getFinalEndpoint())
            		.addParameter("client_id", clientId)
            		.addParameter("response_type", "code")
            		.addParameter("redirect_uri", clientRedirectUri)
            		.addParameter("scope", scopes)
            		.build();
		} catch (URISyntaxException e){
			throw new ConnectionInitializationException("Unable to create redirect URI", e);
		}
    	return returnUri;
    }  
    
    /**
     * Provide an {@link AccessToken} for the given parameters of this service and the given {@link HttpResponse}.
     * If the User acepted your request for the needed data you will get an access token. 
     * If the User denied your request a {@link ForbiddenException} will be thrown.
     * If the {@linkplain HttpResponse} does not contain a value named "code" or "error" a 
     * {@linkplain InvalidAttributeException} will be thrown
     * @param authCodeResponse response goven from the OSIAM server. 
     * For more information please look at the wiki at github
     * @return a valid AccessToken
     */
    public AccessToken retrieveAccessToken(HttpResponse authCodeResponse) {
		String authCode = null;
    	Header header = authCodeResponse.getLastHeader("Location");
		HeaderElement[] elements = header.getElements();
		for (HeaderElement actHeaderElement : elements) {
			if(actHeaderElement.getName().contains("code")){// NOSONAR - false-positive from clover; if-expression is correct
				authCode = actHeaderElement.getValue();
				break;
			}
			if(actHeaderElement.getName().contains("error")){// NOSONAR - false-positive from clover; if-expression is correct
				throw new ForbiddenException("The user had denied the acces to his data.");
			}
		}
		if(authCode == null){// NOSONAR - false-positive from clover; if-expression is correct
			throw new InvalidAttributeException("Could not find any auth code or error message in the given Response");
		}
    	return retrieveAccessToken(authCode);
    }
    
    /**
     * Provide an {@link AccessToken} for the given parameters of this service and the given authCode.
     * @param authCode authentication code retrieved from the OSIAM Server by using the oauth2 login flow. 
     * For more information please look at the wiki at github
     * @return a valid AccessToken
     */
    public AccessToken retrieveAccessToken(String authCode) {
        if (authCode == null) { // NOSONAR - false-positive from clover; if-expression is correct
            throw new IllegalArgumentException("The given authentication code can't be null.");
        }

        HttpPost realWebResource = getWebRessourceToEchangeAuthCode(authCode);
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        int httpStatus = 0;
        try{
            response = httpclient.execute(realWebResource);
            httpStatus = response.getStatusLine().getStatusCode();
        }catch(IOException e){
        	throw new ConnectionInitializationException("Unable to setup connection", e); 
        }
        
        if (httpStatus != SC_OK) { // NOSONAR - false-positive from clover; if-expression is correct
            String errorMessage;
            switch (httpStatus) {
                case SC_BAD_REQUEST:
                    errorMessage = getErrorMessage(response, "Could not exchange yout authentication code against a access token.");
                    throw new ConflictException(errorMessage);
                default:
                    errorMessage = getErrorMessage(response, String.format("Unable to setup connection (HTTP Status Code: %d)", httpStatus));
                    throw new ConnectionInitializationException(errorMessage);
            }
        }

        return getAccessToken(response);
    }  
    
    private AccessToken getAccessToken(HttpResponse response){
        final AccessToken accessToken;
        try {
            InputStream content = response.getEntity().getContent();
            ObjectMapper mapper = new ObjectMapper();
            accessToken = mapper.readValue(content, AccessToken.class);
        } catch (IOException e) {
            throw new ConnectionInitializationException("Unable to retrieve access token: IOException", e);
        }
        return accessToken;	
    }
    
    private String getFinalEndpoint(){
    	String finalEndpoint = endpoint;
    	if(grantType.equals(GrantType.AUTHORIZATION_CODE)){// NOSONAR - we check before that the grantType is not null
    		 finalEndpoint += "/oauth/authorize";
    	}else{
    		finalEndpoint += "/oauth/token";
    	}
    	return finalEndpoint;
    }
    
    private HttpPost getWebRessourceToEchangeAuthCode(String authCode){
    	 HttpPost realWebResource = new HttpPost(endpoint + "/oauth/token");
         String authHeaderValue = "Basic " + encodeClientCredentials(clientId, clientSecret);
         realWebResource.addHeader("Authorization", authHeaderValue);

         List<NameValuePair> nameValuePairs = new ArrayList<>();
         nameValuePairs.add(new BasicNameValuePair("code", authCode));
         nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
         nameValuePairs.add(new BasicNameValuePair("redirect_uri", clientRedirectUri));
         
         try{
         	realWebResource.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
         }catch(UnsupportedEncodingException e){
         	throw new ConnectionInitializationException("Unable to Build Request in this encoding.", e);
         }
         return realWebResource;
    }
    
    /**
     * The Builder class is used to construct instances of the {@link AuthService}.
     */
    public static class Builder {

        private String clientId;
        private String clientSecret;
        private GrantType grantType;      
        private String scopes;
        private String endpoint;
        private String password;
        private String userName;
        private String clientRedirectUri;

        /**
         * Set up the Builder for the construction of  an {@link AuthService} instance for the OAuth2 service at
         * the given endpoint
         *
         * @param endpoint The URL at which the OAuth2 service lives.
         */
        public Builder(String endpoint) {
            this.endpoint = endpoint;
        }

        /**
         * Use the given {@link Scope} to for the request. 
         * @param scope the needed scope
         * @param scopes the needed scopes
         * @return The builder itself
         */
        public Builder setScope(Scope scope, Scope... scopes){
        	List<Scope> scopeList = new ArrayList<>();
        	scopeList.add(scope);
        	for (Scope actScope : scopes) {
        		scopeList.add(actScope);
			}
        	if(scopeList.contains(Scope.ALL)){// NOSONAR - false-positive from clover; if-expression is correct
        		this.scopes = Scope.ALL.toString();
        	}else{
        		StringBuilder scopeBuilder = new StringBuilder();
        		for (Scope actScope : scopeList) {
					scopeBuilder.append(" ").append(actScope.toString());
        		}
        		this.scopes = scopeBuilder.toString().trim();
        	}
        	return this;
        }
        
        /**
         * The needed access token scopes as String like 'GET PATCH' 
         * @param scope the needed scope
         * @return The builder itself
         */
        public Builder setScope(String scope){
        	this.scopes = scope;
        	return this;
        }
        
        /**
         * Use the given {@link GrantType} to for the request. 
         *
         * @param grantType of the requested AuthCode
         * @return The builder itself
         * @throws UnsupportedOperationException If the GrantType is anything else than GrantType.PASSWORD
         */
        public Builder setGrantType(GrantType grantType) {
            this.grantType = grantType;
            return this;
        }

        /**
         * Add a ClientId to the OAuth2 request
         *
         * @param clientId The client-Id
         * @return The builder itself
         */
        public Builder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }
        
        /**
         * Add a Client redirect URI to the OAuth2 request
         *
         * @param clientRedirectUri the clientRedirectUri which is known to the OSIAM server
         * @return The builder itself
         */
        public Builder setClientRedirectUri(String clientRedirectUri) {
			this.clientRedirectUri = clientRedirectUri;
            return this;
        }
        
        /**
         * Add a clientSecret to the OAuth2 request
         *
         * @param clientSecret The client secret
         * @return The builder itself
         */
        public Builder setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        /**
         * Add the given username to the OAuth2 request
         *
         * @param userName The username
         * @return The builder itself
         */
        public Builder setUsername(String userName) {
        	this.userName = userName;
            return this;
        }

        /**
         * Add the given password to the OAuth2 request
         *
         * @param password The password
         * @return The builder itself
         */
        public Builder setPassword(String password) {
        	this.password = password;
            return this;
        }

        /**
         * Construct the {@link AuthService} with the parameters passed to this builder.
         *
         * @return An AuthService configured accordingly.
         * @throws ConnectionInitializationException
         *          If either the provided client credentials (clientId/clientSecret)
         *          or, if the requested grant type is 'password', the user credentials (userName/password) are incomplete.
         */
        public AuthService build() {
            ensureAllNeededParameterAreCorrect();
            return new AuthService(this);
        }
        
        private void ensureAllNeededParameterAreCorrect(){// NOSONAR - this is a test method the Cyclomatic Complexity can be over 10.
            if (clientId == null || clientSecret == null) { // NOSONAR - false-positive from clover; if-expression is correct
                throw new IllegalArgumentException("The provided client credentials are incomplete.");
            }
            if(scopes == null){// NOSONAR - false-positive from clover; if-expression is correct
            	throw new IllegalArgumentException("At least one scope needs to be set.");
            }
            if (grantType == null) { // NOSONAR - false-positive from clover; if-expression is correct
                throw new IllegalArgumentException("The grant type is not set.");
            }
            if (grantType.equals(GrantType.PASSWORD) && (userName == null && password == null)) { // NOSONAR - false-positive from clover; if-expression is correct
                throw new IllegalArgumentException("The grant type 'password' requires username and password");
            }
            if ((grantType.equals(GrantType.CLIENT_CREDENTIALS) || grantType.equals(GrantType.AUTHORIZATION_CODE))// NOSONAR - false-positive from clover; if-expression is correct
            		&& (userName != null || password != null)) { // NOSONAR - false-positive from clover; if-expression is correct
                throw new IllegalArgumentException("For the grant type '" + grantType + "' setting of password and username are not allowed.");
            }
            if (grantType.equals(GrantType.AUTHORIZATION_CODE) && clientRedirectUri == null) { // NOSONAR - false-positive from clover; if-expression is correct
                throw new IllegalArgumentException("For the grant type '" + grantType + "' the redirect Uri is needed.");
            }
        }
    }
}
