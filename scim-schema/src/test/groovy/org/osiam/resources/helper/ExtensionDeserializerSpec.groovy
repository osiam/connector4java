package org.osiam.resources.helper

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.extension.FieldType
import spock.lang.Specification
import spock.lang.Unroll

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
        FieldType.STRING  | '{"key" : "example"}' | 'example'
        FieldType.INTEGER | '{"key" : 123}'       | 123G
        FieldType.DECIMAL | '{"key" : 12.3}'      | 12.3G
        FieldType.BOOLEAN | '{"key" : true}'      | true

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
