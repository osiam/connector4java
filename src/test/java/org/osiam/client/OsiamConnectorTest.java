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
