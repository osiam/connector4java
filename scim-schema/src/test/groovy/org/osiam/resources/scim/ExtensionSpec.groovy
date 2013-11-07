package org.osiam.resources.scim

import org.osiam.resources.scim.Extension.Field
import org.osiam.test.util.DateHelper

import spock.lang.Specification
import spock.lang.Unroll


class ExtensionSpec extends Specification {

    static def FIELD = 'foo'
    static def VALUE = 'bar'

    static def FIELD_INJECTED = 'injected'
    static def DEFAULT_FIELD_TYPE = ExtensionFieldType.STRING
    static def URN = 'irrelevant'

    def extension

    def 'Query for existing field returns value'() {
        given:
        extensionWithValue()

        expect:
        extension.getField(FIELD, ExtensionFieldType.STRING) == VALUE
    }

    def 'Query with null field type raises exception'() {
        given:
        emptyExtension()

        when:
        extension.getField(FIELD, null)

        then:
        thrown(IllegalArgumentException)
    }

    @Unroll
    def 'Query for a field with name #name raises exception'() {
        given:
        emptyExtension()

        when:
        extension.getField(value, DEFAULT_FIELD_TYPE)

        then:
        thrown(exception)

        where:
        name          | value | exception
        'null'        | null  | IllegalArgumentException
        'empty'       | ''    | IllegalArgumentException
        'nonexistant' | FIELD | NoSuchElementException
    }

    @Unroll
    def 'Adding field with type #givenFieldType adds field to extension'() {
        given:
        emptyExtension()

        when:
        extension.addOrUpdateField(FIELD, givenInputValue)

        then:
        extension.fields[FIELD].value == expectedOutputValue
        extension.fields[FIELD].type == givenFieldType

        where:
        givenFieldType               | givenInputValue                               | expectedOutputValue
        ExtensionFieldType.STRING    | 'example'                                     | 'example'
        ExtensionFieldType.INTEGER   | 123G                                          | '123'
        ExtensionFieldType.DECIMAL   | 12.3G                                         | '12.3'
        ExtensionFieldType.BOOLEAN   | true                                          | 'true'
        ExtensionFieldType.DATE_TIME | DateHelper.createDate(2008, 0, 23, 4, 56, 22) | '2008-01-23T04:56:22.000Z'
        ExtensionFieldType.BINARY    | [101, 120, 97, 109, 112, 108, 101] as byte[]  | 'ZXhhbXBsZQ=='
        ExtensionFieldType.REFERENCE | new URI('https://example.com/Users/28')       | 'https://example.com/Users/28'
    }

    @Unroll
    def 'Updating field with type #givenFieldType updates field in extension'() {
        given:
        extensionWithValue()

        when:
        extension.addOrUpdateField(FIELD, givenInputValue)

        then:
        extension.fields[FIELD].value == expectedOutputValue
        extension.fields[FIELD].type == givenFieldType

        where:
        givenFieldType      | givenInputValue                              | expectedOutputValue
        ExtensionFieldType.STRING    | 'example'                                    | 'example'
        ExtensionFieldType.INTEGER   | 123G                                         | '123'
        ExtensionFieldType.DECIMAL   | 12.3G                                        | '12.3'
        ExtensionFieldType.BOOLEAN   | true                                         | 'true'
        ExtensionFieldType.DATE_TIME | DateHelper.createDate(2008, 0, 23, 4, 56, 22)           | '2008-01-23T04:56:22.000Z'
        ExtensionFieldType.BINARY    | [101, 120, 97, 109, 112, 108, 101] as byte[] | 'ZXhhbXBsZQ=='
        ExtensionFieldType.REFERENCE | new URI('https://example.com/Users/28')      | 'https://example.com/Users/28'
    }

    @Unroll
    def 'Adding/Updating a field with a #testCase name raises exception'() {
        given:
        emptyExtension()

        when:
        extension.addOrUpdateField(fieldName, VALUE)

        then:
        thrown(expectedException)

        where:
        testCase | fieldName | expectedException
        'null'   | null      | IllegalArgumentException
        'empty'  | ''        | IllegalArgumentException
    }

    def 'Adding/Updating a field with null value raises exception'() {
        given:
        emptyExtension()

        when:
        extension.addOrUpdateField(FIELD, (String) null)

        then:
        thrown(IllegalArgumentException)
    }

    def 'getAllFields returns a map of all the fields'() {
        given:

        Extension extension = new Extension(URN)
        extension.addOrUpdateField(FIELD, VALUE)
        extension.addOrUpdateField(VALUE, FIELD)
        when:
        def result = extension.allFields
        then:
        result.size() == 2
        result[FIELD] == new Field(DEFAULT_FIELD_TYPE, VALUE)
        result[VALUE] == new Field(DEFAULT_FIELD_TYPE, FIELD)
    }

    def 'getAllFields returns an immutable map'() {
        given:
        Extension extension = new Extension(URN)
        extension.addOrUpdateField(FIELD, VALUE)
        def result = extension.getAllFields()

        when:
        result[FIELD] = FIELD

        then:
        thrown(UnsupportedOperationException)
    }

    def 'isFieldPresent should return true when field is present'() {
        given:
        extensionWithValue()

        expect:
        extension.isFieldPresent(FIELD) == true
    }

    def 'isFieldPresent should return false when field is not present'() {
        given:
        extensionWithValue()

        expect:
        extension.isFieldPresent(FIELD_INJECTED) == false
    }

    private def emptyExtension() {
        extension = new Extension()
    }

    private extensionWithValue() {
        extension = new Extension(URN)
        extension.addOrUpdateField(FIELD, VALUE)

    }

}
