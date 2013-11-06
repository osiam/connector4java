package org.osiam.resources.scim

import org.osiam.resources.scim.extension.FieldType
import spock.lang.Specification
import spock.lang.Unroll

class ExtensionSpec extends Specification {

    static def FIELD = 'foo'
    static def VALUE = 'bar'

    static def FIELD_INJECTED = 'injected'
    static def DEFAULT_FIELD_TYPE = FieldType.STRING
    static def URN = 'irrelevant'

    def extension




    def 'Query for existing field returns value'() {
        given:
        extensionWithValue()

        expect:
        extension.getField(FIELD, FieldType.STRING) == VALUE
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
        givenFieldType      | givenInputValue                              | expectedOutputValue
        FieldType.STRING    | 'example'                                    | 'example'
        FieldType.INTEGER   | 123G                                         | '123'
        FieldType.DECIMAL   | 12.3G                                        | '12.3'
        FieldType.BOOLEAN   | true                                         | 'true'
        FieldType.DATE_TIME | createDate(2008, 0, 23, 4, 56, 22)           | '2008-01-23T04:56:22.000Z'
        FieldType.BINARY    | [101, 120, 97, 109, 112, 108, 101] as byte[] | 'ZXhhbXBsZQ=='
        FieldType.REFERENCE | new URI('https://example.com/Users/28')      | 'https://example.com/Users/28'
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
        FieldType.STRING    | 'example'                                    | 'example'
        FieldType.INTEGER   | 123G                                         | '123'
        FieldType.DECIMAL   | 12.3G                                        | '12.3'
        FieldType.BOOLEAN   | true                                         | 'true'
        FieldType.DATE_TIME | createDate(2008, 0, 23, 4, 56, 22)           | '2008-01-23T04:56:22.000Z'
        FieldType.BINARY    | [101, 120, 97, 109, 112, 108, 101] as byte[] | 'ZXhhbXBsZQ=='
        FieldType.REFERENCE | new URI('https://example.com/Users/28')      | 'https://example.com/Users/28'
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

    private def createDate(int year, int month, int date, int hourOfDay, int minute,
                           int second) {
        Calendar calendar = Calendar.getInstance()
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.setTimeZone(TimeZone.getTimeZone(TimeZone.GMT_ID))
        calendar.set(year, month, date, hourOfDay, minute, second)
        calendar.getTime()
    }

}
