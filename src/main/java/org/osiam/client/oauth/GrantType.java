package org.osiam.client.oauth;

/**
 * The Grant Type represents the type of Grant that the client expects from the service.
 * At this point the following grant types are supported.
 * <ul>
 *     <li>AUTHORIZATION_CODE</li>
 *     <li>CLIENT_CREDENTIALS</li>
 *     <li>PASSWORD</li>
 *     <li>REFRESH_TOKEN</li>
 * </ul>
 */
public enum GrantType {

    AUTHORIZATION_CODE("authorization_code"),
    CLIENT_CREDENTIALS("client_credentials"),
    PASSWORD("password"),
    REFRESH_TOKEN("refresh-token");

    private String urlParam;

    GrantType(String urlParam) {
        this.urlParam = urlParam;
    }

    /**
     * Provide the string for use in the actual request.
     * @return The string representation of the grant type.
     */
    public String getUrlParam() {
        return urlParam;
    }

}
