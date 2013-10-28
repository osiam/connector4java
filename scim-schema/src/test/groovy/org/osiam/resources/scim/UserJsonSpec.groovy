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

import org.codehaus.jackson.map.ObjectMapper
import org.osiam.test.util.JsonFixturesHelper

import spock.lang.Specification
import spock.lang.Unroll;

class UserJsonSpec extends Specification {
    
    private final static ObjectMapper mapper = new ObjectMapper()
    
    @Unroll
    def 'A #userType User is correctly serialized' () {
        when:
            def result = mapper.writeValueAsString(user)
        then:
            result == expectedJson
            
        where:
            userType   | expectedJson                          | user
            'simple'   | JsonFixturesHelper.JSON_SIMPLE_USER   | JsonFixturesHelper.mapSimpleUser()
            'basic'    | JsonFixturesHelper.JSON_BASIC_USER    | JsonFixturesHelper.mapBasicUser()
            'extended' | JsonFixturesHelper.JSON_EXTENDED_USER | JsonFixturesHelper.mapExtendedUser()
    }

}
