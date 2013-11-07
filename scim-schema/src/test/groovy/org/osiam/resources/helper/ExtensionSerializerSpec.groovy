package org.osiam.resources.helper

import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.extension.FieldType
import org.osiam.test.util.DateHelper;
import org.skyscreamer.jsonassert.JSONAssert;

import spock.lang.Specification
import spock.lang.Unroll

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule

class ExtensionSerializerSpec  extends Specification{

    def 'serializing an empty extension works'(){
        given:
        ObjectMapper mapper = new ObjectMapper()
        Extension extension = new Extension('extension')
        
        expect:
        JSONAssert.assertEquals('{}', mapper.writeValueAsString(extension), false);
    }
    
    @Unroll
    def 'serializing an extension with #type type works'() {
        given:
        ObjectMapper mapper = new ObjectMapper()
        Extension extension = new Extension('extension')
        extension.addOrUpdateField('key', givenValue)
        
        expect:
        JSONAssert.assertEquals(expectedJson, mapper.writeValueAsString(extension), false);

        where:
        type                | givenValue                                   | expectedJson
        FieldType.STRING    | 'example'                                    | '{"key" : "example"}'
        FieldType.INTEGER   | 123G                                         | '{"key" : 123}'
        FieldType.DECIMAL   | 12.3G                                        | '{"key" : 12.3}'
        FieldType.BOOLEAN   | true                                         | '{"key" : true}'
        FieldType.DATE_TIME | DateHelper.createDate(2008, 0, 23, 4, 56, 22)           | '{"key" : "2008-01-23T04:56:22.000Z"}'
        FieldType.BINARY    | [101, 120, 97, 109, 112, 108, 101] as byte[] | '{"key" : "ZXhhbXBsZQ=="}'
        FieldType.REFERENCE | new URI('https://example.com/Users/28')      | '{"key" : "https://example.com/Users/28"}'
    }
}
