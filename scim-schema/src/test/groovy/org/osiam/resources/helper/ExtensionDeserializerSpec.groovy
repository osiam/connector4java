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

import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.ExtensionFieldType

import spock.lang.Specification
import spock.lang.Unroll

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule

class ExtensionDeserializerSpec extends Specification {

    def 'deserializing extension sets URN'() {

        given:
        def urn = 'extension'
        def mapper = configuredObjectMapper(urn)
        def json = '{}'

        expect:
        mapper.readValue(json, Extension).getUrn() == urn
    }

    @Unroll
    def 'deserializing an extension with #testCase urn raises exception'() {
        given:
        def mapper = configuredObjectMapper(urn)
        def json = '{}'

        when:
        mapper.readValue(json, Extension);

        then:
        thrown(IllegalStateException)

        where:
        testCase | urn
        'null'   | null
        'empty'  | ''
    }

    @Unroll
    def 'deserializing an extension with #type type works'() {
        given:
        def mapper = configuredObjectMapper('extension')

        when:
        Extension extension = mapper.readValue(json, Extension);

        then:
        extension.getField('key', type) == result

        where:
        type              | json                  | result
        ExtensionFieldType.STRING  | '{"key" : "example"}' | 'example'
        ExtensionFieldType.INTEGER | '{"key" : 123}'       | 123G
        ExtensionFieldType.DECIMAL | '{"key" : 12.3}'      | 12.3G
        ExtensionFieldType.BOOLEAN | '{"key" : true}'      | true
    }

    private static def configuredObjectMapper(String urn) {
        ObjectMapper mapper = new ObjectMapper()
        def extensionDeserializer = new ExtensionDeserializer(Extension)
        SimpleModule testModule = new SimpleModule('MyModule', new Version(1, 0, 0, null))
                .addDeserializer(Extension, extensionDeserializer)
        mapper.registerModule(testModule)
        extensionDeserializer.setUrn(urn)
        return mapper
    }
}
