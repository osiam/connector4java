package org.osiam.model;
/*
 * for licensing see in the license.txt
 */
import java.util.Date;

/**
* This model class holds the information about an access token.
* The access token is used to get access to a OSIAM server and to get and 
* manipulate users and groups.
* An instance of this class get be created by the AuthService.
 */
public class AccessToken {
	private String authId;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private String scope;
    private Date createdOn;
    private Date expiresAt;

    /**
     * Retrieve the Authentication ID used to retrieve the AccessToken
     * @return The AuthId string
     */
	public String getAuthId() {
		return authId;
	}

	/**
	 * Set the Authentication ID to relate between the this ID and the AccessToken
	 * retrieved with this one
	 * @param authId The AuthId as string
	 */
	public void setAuthId(String authId) {
		this.authId = authId;
	}
	
	/**
	 * Retrieve the string value of the AccessToken
	 * @return The access token string
	 */
    public String getAccessToken() {
        return accessToken;
    }

	/**
	 * Set the string value of the AccessToken
	 * @param accessToken The access token string
	 */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * TODO is it possible to do a refreshing with OSIAM?
     * @return
     */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * TODO is it possible to do a refreshing with OSIAM?
	 * @param refreshToken
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
    
    /**
     * TODO not sure which types are possible and why we need them
     * @return
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * TODO not sure which types are possible and why we need them
     * @param tokenType
     */
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    /**
     *TODO do we need this if we have the expiresAt Date
     * @return
     */
    public long getExpiresIn() {
        return expiresIn;
    }
    /**
     * TODO do we need this if we have the expiresAt Date
     * @param expiresIn
     */
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    /**
     * Retrieve the possible Scopes of this AccessToken
     * @return The scopes as string
     */
    public String getScope() {
        return scope;
    }

    /**
     * Set the wanted scopes of the AccessToken
     * @param scope the scopes as string
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Retrieve the Date when the AccessToken was created
     * @return The creation Date
     */
	public Date getCreatedOn() {
		return createdOn;
	}

	/**
	 * Set the Date when the AccessToken was created
	 * @param createdOn The creation Date
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * Retrieve the Date when the AccessToken will be expired 
	 * @return The expiring Date of the AccessToken
	 */
	public Date getExpiresAt() {
		return expiresAt;
	}

	/**
	 * Set the Expire Date when the AccessToken after 
	 * the AccessToken will not be valid anymore
	 * @param expiresAt The expiring Date
	 */
	public void setExpiresAt(Date expiresAt) {
		this.expiresAt = expiresAt;
	}
	
    @Override
    /**
     * retrieves all main Information string based
     */
    public String toString() {
    	StringBuilder returnToken = new StringBuilder();
    	
    	returnToken.append("AccessToken [").
    				append("access token = ").append(accessToken).
    				append(", token type = ").append(tokenType).
    				append(", scope = ").append(scope).
    				append(", created on = ").append(createdOn).
    				append(", expired at = ").append(expiresAt).
    				append("]");
    			
        return returnToken.toString();
    }
}
