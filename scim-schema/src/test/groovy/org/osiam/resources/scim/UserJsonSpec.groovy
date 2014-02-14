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

package org.osiam.resources.scim

import org.osiam.test.util.JsonFixturesHelper
import org.skyscreamer.jsonassert.JSONAssert

import spock.lang.Specification
import spock.lang.Unroll

import com.fasterxml.jackson.databind.ObjectMapper

class UserJsonSpec extends Specification {

    private static JsonFixturesHelper jsonFixtures = new JsonFixturesHelper();

    private final static ObjectMapper mapper = new ObjectMapper()

    @Unroll
    def 'A #userType User is correctly serialized' () {
        when:
        def json = mapper.writeValueAsString(user)
        then:
        println json
        JSONAssert.assertEquals(expectedJson, json, false);

        where:
        userType   | expectedJson                  | user
        'simple'   | jsonFixtures.jsonSimpleUser   | mapSimpleUser()
        'basic'    | jsonFixtures.jsonBasicUser    | mapBasicUser()
        'extended' | jsonFixtures.jsonExtendedUser | mapExtendedUser()
    }

    private User mapBasicUser(){
        jsonFixtures.configuredObjectMapper().readValue(jsonFixtures.jsonBasicUser, User)
    }
    private User mapSimpleUser(){
        jsonFixtures.configuredObjectMapper().readValue(jsonFixtures.jsonSimpleUser, User)
    }
    private User mapExtendedUser(){
        jsonFixtures.configuredObjectMapper().readValue(jsonFixtures.jsonExtendedUser, User)
    }
}
