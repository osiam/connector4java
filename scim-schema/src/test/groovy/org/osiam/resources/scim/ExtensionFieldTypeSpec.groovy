package org.osiam.resources.scim

import java.nio.ByteBuffer

import org.osiam.test.util.DateHelper

import spock.lang.Specification
import spock.lang.Unroll

class ExtensionFieldTypeSpec extends Specification {

    @Unroll
    def 'getName on FieldType.#givenTypeInstance returns #expectedTypeName'() {
        when:
        def typeName = givenTypeInstance.getName()

        then:
        typeName == expectedTypeName

        where:
        givenTypeInstance    | expectedTypeName
        ExtensionFieldType.STRING     | 'STRING'
        ExtensionFieldType.INTEGER    | 'INTEGER'
        ExtensionFieldType.DECIMAL    | 'DECIMAL'
        ExtensionFieldType.BOOLEAN    | 'BOOLEAN'
        ExtensionFieldType.DATE_TIME  | 'DATE_TIME'
        ExtensionFieldType.BINARY     | 'BINARY'
        ExtensionFieldType.REFERENCE  | 'REFERENCE'
    }

    @Unroll
    def 'fromString on FieldType.#givenTypeInstance returns the correctly typed value'(){
        when:
        def output = givenTypeInstance.fromString(givenInputValue)

        then:
        output == expectedOutputValue

        where:
        givenTypeInstance    | givenInputValue                | expectedOutputValue
        ExtensionFieldType.STRING     | 'example'                      | 'example'
        ExtensionFieldType.INTEGER    | '123'                          | 123G
        ExtensionFieldType.DECIMAL    | '12.3'                         | 12.3G
        ExtensionFieldType.BOOLEAN    | 'true'                         | true
        ExtensionFieldType.DATE_TIME  | '2011-08-01T18:29:49.000Z'     | DateHelper.createDate(2011, 7, 1, 18, 29, 49)
        ExtensionFieldType.BINARY     | 'ZXhhbXBsZQ=='                 | ByteBuffer.wrap([101, 120, 97, 109, 112, 108, 101] as byte[])
        ExtensionFieldType.REFERENCE  | 'https://example.com/Users/28' | new URI('https://example.com/Users/28')
    }

    @Unroll
    def 'fromString on FieldType.#givenTypeInstance with illegal value raises exception'(){
        when:
        givenTypeInstance.fromString(givenInputValue)

        then:
        thrown(IllegalArgumentException)

        where:
        givenTypeInstance    | givenInputValue
        ExtensionFieldType.INTEGER    | 'illegal'
        ExtensionFieldType.DECIMAL    | 'illegal'
        ExtensionFieldType.DATE_TIME  | 'illegal'
        ExtensionFieldType.BINARY     | '!@#$%^&*()_+'
        ExtensionFieldType.REFERENCE  | '!@#$%^&*()_+'
    }

    @Unroll
    def 'fromString on FieldType.#givenTypeInstance with null value raises exception'(){
        when:
        givenTypeInstance.fromString(givenInputValue)

        then:
        thrown(IllegalArgumentException)

        where:
        givenTypeInstance    | givenInputValue
        ExtensionFieldType.STRING     | null
        ExtensionFieldType.INTEGER    | null
        ExtensionFieldType.DECIMAL    | null
        ExtensionFieldType.BOOLEAN    | null
        ExtensionFieldType.DATE_TIME  | null
        ExtensionFieldType.BINARY     | null
        ExtensionFieldType.REFERENCE  | null
    }

    @Unroll
    def 'toString on FieldType.#givenTypeInstance with null value raises exception'(){
        when:
        givenTypeInstance.toString(givenInputValue)

        then:
        thrown(IllegalArgumentException)

        where:
        givenTypeInstance    | givenInputValue
        ExtensionFieldType.STRING     | null
        ExtensionFieldType.INTEGER    | null
        ExtensionFieldType.DECIMAL    | null
        ExtensionFieldType.BOOLEAN    | null
        ExtensionFieldType.DATE_TIME  | null
        ExtensionFieldType.BINARY     | null
        ExtensionFieldType.REFERENCE  | null
    }

    @Unroll
    def 'toString on FieldType.#givenTypeInstance returns the correct String value'(){

        when:
        def output = givenTypeInstance.toString(givenInputValue)

        then:
        output == expectedOutputValue

        where:
        givenTypeInstance             | givenInputValue                                               | expectedOutputValue
        ExtensionFieldType.STRING     | 'example'                                                     | 'example'
        ExtensionFieldType.INTEGER    | 123G                                                          | '123'
        ExtensionFieldType.DECIMAL    | 12.3G                                                         | '12.3'
        ExtensionFieldType.BOOLEAN    | true                                                          | 'true'
        ExtensionFieldType.DATE_TIME  | DateHelper.createDate(2008, 0, 23, 4, 56, 22)                 | '2008-01-23T04:56:22.000Z'
        ExtensionFieldType.BINARY     | ByteBuffer.wrap([101, 120, 97, 109, 112, 108, 101] as byte[]) | 'ZXhhbXBsZQ=='
        ExtensionFieldType.REFERENCE  | new URI('https://example.com/Users/28')                       | 'https://example.com/Users/28'
    }

    @Unroll
    def 'retrieving FieldType instance via static valueOf with "#typeName" returns FieldType.#expectedTypeInstance'() {
        when:
        def typeInstance = ExtensionFieldType.valueOf(typeName)

        then:
        typeInstance == expectedTypeInstance

        where:
        typeName    | expectedTypeInstance
        'STRING'    | ExtensionFieldType.STRING
        'INTEGER'   | ExtensionFieldType.INTEGER
        'DECIMAL'   | ExtensionFieldType.DECIMAL
        'BOOLEAN'   | ExtensionFieldType.BOOLEAN
        'DATE_TIME' | ExtensionFieldType.DATE_TIME
        'BINARY'    | ExtensionFieldType.BINARY
        'REFERENCE' | ExtensionFieldType.REFERENCE
    }

    def 'retrieving FieldType instance via static valueOf with unknown type name raises exception'() {
        given:
        def unknownTypeName = 'unknown'

        when:
        ExtensionFieldType.valueOf(unknownTypeName)

        then:
        thrown(IllegalArgumentException)
    }
}
