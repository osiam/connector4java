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

package org.osiam.resources.helper

import spock.lang.Ignore

import java.nio.ByteBuffer

import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.ExtensionFieldType
import org.osiam.test.util.DateHelper

import spock.lang.Specification
import spock.lang.Unroll

import com.fasterxml.jackson.databind.ObjectMapper

class ExtensionSerializerSpec  extends Specification{

    @Ignore('''We cannot use JSONAssert anymore, because of licensing issues. This
            test has to be re-activated when:

              1) JSONAssert fixes its licensing issues (see https://github.com/skyscreamer/JSONassert/issues/44)
              2) An alternative library for comparing JSON has been found

            Beware of the following: Whenever you change things in this project
            that might affect the generated JSON you HAVE TO re-activate this
            test, either using method 1), 2), or implementing an own JSON
            test mechanism!''')
    def 'serializing an empty extension works'(){
        given:
        ObjectMapper mapper = new ObjectMapper()
        Extension extension = new Extension.Builder('extension').build()

        expect:
        false
    }

    @Ignore('''We cannot use JSONAssert anymore, because of licensing issues. This
            test has to be re-activated when:

              1) JSONAssert fixes its licensing issues (see https://github.com/skyscreamer/JSONassert/issues/44)
              2) An alternative library for comparing JSON has been found

            Beware of the following: Whenever you change things in this project
            that might affect the generated JSON you HAVE TO re-activate this
            test, either using method 1), 2), or implementing an own JSON
            test mechanism!''')
    @Unroll
    def 'serializing an extension with #type type works'() {
        given:
        ObjectMapper mapper = new ObjectMapper()
        Extension extension = new Extension.Builder('extension').setField('key', givenValue).build()

        expect:
        false

        where:
        type                         | givenValue                                                     | expectedJson
        ExtensionFieldType.STRING    | 'example'                                                      | '{"key" : "example"}'
        ExtensionFieldType.INTEGER   | 123G                                                           | '{"key" : 123}'
        ExtensionFieldType.DECIMAL   | 12.3G                                                          | '{"key" : 12.3}'
        ExtensionFieldType.BOOLEAN   | true                                                          | '{"key" : true}'
        ExtensionFieldType.DATE_TIME | DateHelper.createDate(2008, 0, 23, 4, 56, 22)                  | '{"key" : "2008-01-23T04:56:22.000Z"}'
        ExtensionFieldType.BINARY    | ByteBuffer.wrap([
            101,
            120,
            97,
            109,
            112,
            108,
            101] as byte[]) | '{"key" : "ZXhhbXBsZQ=="}'
        ExtensionFieldType.REFERENCE | new URI('https://example.com/Users/28')                        | '{"key" : "https://example.com/Users/28"}'
    }
}
