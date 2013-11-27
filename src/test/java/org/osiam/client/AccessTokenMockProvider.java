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

package org.osiam.client;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.osiam.client.oauth.AccessToken;

public class AccessTokenMockProvider {

    private static final String INVALID_ACCESS_TOKEN = "99d116cb-2758-4e7c-9aca-4a115bc4f19e";
    private ObjectMapper mapper;
    private String path;

    public AccessTokenMockProvider(String path) {
        this.path = path;
        mapper = new ObjectMapper();
    }

    public AccessToken valid_access_token() throws IOException {
        InputStream is = readFile(path);
        return mapper.readValue(is, AccessToken.class);
    }

    public AccessToken expired_access_token() throws IOException, NoSuchFieldException, IllegalAccessException {
        InputStream is = readFile(path);
        AccessToken accessToken = mapper.readValue(is, AccessToken.class);
        Field expiredInField = accessToken.getClass().getDeclaredField("expiresIn");
        expiredInField.setAccessible(true);
        expiredInField.set(accessToken, -1);
        expiredInField.setAccessible(false);
        return accessToken;
    }

    public AccessToken invalid_access_token() throws IOException, NoSuchFieldException, IllegalAccessException {
        InputStream is = readFile(path);
        AccessToken accessToken = mapper.readValue(is, AccessToken.class);
        Field token = accessToken.getClass().getDeclaredField("token");
        token.setAccessible(true);
        token.set(accessToken, INVALID_ACCESS_TOKEN);
        token.setAccessible(false);
        return accessToken;
    }

    private InputStream readFile(String path) {
        return getClass().getResourceAsStream(path);

    }
}
