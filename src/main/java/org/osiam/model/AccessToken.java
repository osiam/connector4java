package org.osiam.model;

/**
 * Created with IntelliJ IDEA.
 * User: dmoeb
 * Date: 10.06.13
 * Time: 09:04
 * To change this template use File | Settings | File Templates.
 */
public class AccessToken {
    String access_token;
    String token_type;
    long expires_in;
    String scope;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "AccessToken [access_token=" + access_token + ", token_type="
                + token_type + ", expires_in=" + expires_in + ", scope="
                + scope + "]";
    }
}
