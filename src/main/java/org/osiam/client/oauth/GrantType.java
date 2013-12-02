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
 * The Grant Type represents the type of Grant that the client expects from the service.
 */
public enum GrantType {

	/**
	 * for this GrantType also the user name and the user password are needed
	 */
    RESOURCE_OWNER_PASSWORD_CREDENTIALS("password")
     /**
	 * for this GrantType the user name and the user password are not allowed
	 * If you set one of these a exception will be thrown.
	 * Also a redir4ect Uri is needed
	 */
   , AUTHORIZATION_CODE("authorization_code")
    /**
	 * for this GrantType the user name and the user password are not allowed
	 * If you set one of these a exception will be thrown
	 */
   , CLIENT_CREDENTIALS("client_credentials");

    private String urlParam;

    GrantType(String urlParam) {
        this.urlParam = urlParam;
    }

    /**
     * Provide the string for use in the actual request.
     *
     * @return The string representation of the grant type.
     */
    public String getUrlParam() {
        return urlParam;
    }

}
