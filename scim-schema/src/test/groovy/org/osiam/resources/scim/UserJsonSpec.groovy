/*
 * Copyright 2013
 *     tarent AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
