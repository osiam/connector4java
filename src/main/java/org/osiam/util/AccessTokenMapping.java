package org.osiam.util;
/*
 * for licensing see in the license.txt
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.osiam.model.AccessToken;

/**
 * TODO
 * 
 *
 */
public class AccessTokenMapping {

	/**
	 * Maps the given InputStream into a new AccessToken
	 * @param content a imputStream that holds a accessToken as Json String
	 * @return a valid AccessToken
	 * @throws JsonParseException 
	 * @throws JsonMappingException in case the inputStream could not be mapped or a field is missing
	 * @throws IOException
	 */
	public AccessToken getAccessToken(InputStream content) throws JsonParseException, JsonMappingException, IOException{
		
		AccessToken accessToken = new AccessToken();
		String key;
		String strValue;
		
		ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		HashMap<String, Object> jsonValuesHashMap = mapper.readValue(content, HashMap.class);
		
		key = "access_token";
		if(!jsonValuesHashMap.containsKey(key)){
			String error = "Can not create a AccessToken. The " + key + " was not given.";
			throw new JsonMappingException(error);
		}
		strValue = jsonValuesHashMap.get(key).toString();
		accessToken.setAccessToken(strValue);
		
		key = "token_type";
		if(!jsonValuesHashMap.containsKey(key)){
			String error = "Can not create a AccessToken. The " + key + " was not given.";
			throw new JsonMappingException(error);
		}
		strValue = jsonValuesHashMap.get(key).toString();
		accessToken.setTokenType(strValue);
		
		key = "scope";
		if(!jsonValuesHashMap.containsKey(key)){
			String error = "Can not create a AccessToken. The " + key + " was not given.";
			throw new JsonMappingException(error);
		}
		strValue = jsonValuesHashMap.get(key).toString();
		accessToken.setScope(strValue);
		
		key = "expires_in";
		if(!jsonValuesHashMap.containsKey(key)){
			String error = "Can not create a AccessToken. The " + key + " was not given.";
			throw new JsonMappingException(error);
		}
		strValue = jsonValuesHashMap.get(key).toString();
		accessToken.setExpiresIn(Long.decode(strValue));
			
		accessToken.setCreatedOn(new Date());
		Calendar calendar = Calendar.getInstance(); 
		calendar.add(Calendar.SECOND, (int) accessToken.getExpiresIn()); //TODO to be checked. If you run it several time it will count down
		accessToken.setExpiresAt(calendar.getTime());
		
		return accessToken;
	}
}
