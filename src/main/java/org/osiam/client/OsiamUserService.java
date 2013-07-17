package org.osiam.client;
/*
 * for licensing see in the license.txt
 */
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.resources.scim.User;

import java.util.UUID;

/**
 * A OSIAM Service which will be connected to the given OSIAM and provides all needed User methods
 */
public class OsiamUserService {

    private WebResource userWebResource;

    /**
     * With a OsiamUserService is it possible to retrieve or to save any User saved in the OSIAM serv
     * The needed WebResource can be build with help of the ServiceBuilder.buildUserService(...) 
     * @param userWebResource a valid WebResource to connect to a given OSIAM server
     */
    public OsiamUserService(WebResource userWebResource) {
        this.userWebResource = userWebResource;
    }

    /**
     * a single User with the given id. Null if no user could be found
     * @param id the uuid from the wanted user
     * @param accessToken the access token from OSIAM for the actual session
     * @return the wanted user or null
     * @exception UnauthorizedException in case you are not authorized. For example the accesstoken is not valid anymore
     * @exception ConnectionInitializationException will be thrown if no connection to the given OSIAM services could be initialized
     */
    public User getUserByUUID(UUID id, String accessToken) {
    	User user = null;
    	try{
    		user = userWebResource.path(id.toString()).
                    header("Authorization", "Bearer " + accessToken).get(User.class); 
    	}catch(UniformInterfaceException e) {
    		if(e.getResponse().getStatus() == 404){
        		//nothing to do. The User doesn't exists and a null will be returned    			
    		}else if(e.getResponse().getStatus() == 404){
    			throw new UnauthorizedException("You are not authorized to access OSIAM. " +
    					"Please check if your access token is valid");
    		}
    		else{
    			throw e;
    		}
    	}catch(ClientHandlerException e){
    		throw new ConnectionInitializationException("Unable to setup connection", e);
		}
        return user;
    }
}