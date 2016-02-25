/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.client;

import org.junit.Test;

public class OsiamConnectorTest {

    @Test(expected = IllegalStateException.class)
    public void throws_illegal_state_exception_when_no_resource_server_is_configured_and_user_is_retrieved() {
        OsiamConnector connector = new OsiamConnector.Builder().setAuthServerEndpoint("irrelevant").build();
        connector.getUser("irrelevant", null);
    }

    @Test(expected = IllegalStateException.class)
    public void throws_illegal_state_exception_when_no_resource_server_is_configured_and_group_is_retrieved() {
        OsiamConnector connector = new OsiamConnector.Builder().setAuthServerEndpoint("irrelevant").build();
        connector.getGroup("irrelevant", null);
    }

    @Test(expected = IllegalStateException.class)
    public void throws_illegal_state_exception_when_no_auth_server_is_configured_and_access_token_is_retrieved() {
        OsiamConnector connector = new OsiamConnector.Builder().setResourceServerEndpoint("irrelevant").build();
        connector.retrieveAccessToken("irrelevant");
    }
}
