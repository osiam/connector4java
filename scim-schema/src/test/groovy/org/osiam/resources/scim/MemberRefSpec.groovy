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

import spock.lang.Ignore
import spock.lang.Specification

import com.fasterxml.jackson.databind.ObjectMapper

class MemberRefSpec extends Specification {

    private ObjectMapper mapper = new ObjectMapper()

    @Ignore('''We cannot use JSONAssert anymore, because of licensing issues. This
            test has to be re-activated when:

              1) JSONAssert fixes its licensing issues (see https://github.com/skyscreamer/JSONassert/issues/44)
              2) An alternative library for comparing JSON has been found

            Beware of the following: Whenever you change things in this project
            that might affect the generated JSON you HAVE TO re-activate this
            test, either using method 1), 2), or implementing an own JSON
            test mechanism!''')
    def 'serializing member ref results in correct json'() {
        given:
        MemberRef memberRef = new MemberRef.Builder()
                .setReference('irrelevant')
                .build();

        when:
        def json = mapper.writeValueAsString(memberRef)

        then:
        false
    }

    def 'deserializing member ref results in correct MemberRef object'() {
        given:
        String json = '{"$ref":"irrelevant"}'

        when:
        MemberRef memberRef = mapper.readValue(json, MemberRef)

        then:
        memberRef.getReference() == 'irrelevant'
    }
}
