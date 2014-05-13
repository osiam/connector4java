/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.client.oauth;

/**
 * The Grant Type represents the type of oauth2 grant a client wants to use.
 */
public enum GrantType {

    /**
     * For this GrantType client id, client secret, user name and password must
     * be set.
     */
    RESOURCE_OWNER_PASSWORD_CREDENTIALS("password"),
    /**
     * For this GrantType client id, client secret and redirect URI must be set,
     * and user name and password must not be set, otherwise an exception will
     * be thrown.
     */
    AUTHORIZATION_CODE("authorization_code"),
    /**
     * For this GrantType client id and secret must be set, and user name and
     * password must not be set, otherwise an exception will be thrown.
     */
    CLIENT_CREDENTIALS("client_credentials"),
    /**
     * This grant type has the purpose to refresh an existing access token, i.e.
     * getting a new access token with the configured lifetime. Refreshing is
     * not allowed with the client credentials grant.
     */
    REFRESH_TOKEN("refresh_token");

    private String urlParam;

    GrantType(String urlParam) {
        this.urlParam = urlParam;
    }

    /**
     * Provides the string for use in the actual request.
     * 
     * @return The string representation of the grant type.
     */
    public String getUrlParam() {
        return urlParam;
    }

}
