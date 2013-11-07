package org.osiam.resources.helper

import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.ExtensionFieldType
import org.osiam.resources.scim.User
import org.osiam.test.util.DateHelper
import org.osiam.test.util.JsonFixturesHelper

import spock.lang.Specification
import spock.lang.Unroll

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException

class UserDeserializerSpec extends Specification {

    private JsonFixturesHelper jsonFixtures = new JsonFixturesHelper();

    def 'Return a User Instance'() {
        when:
        User user = mapExtendedUser()
        then:
        user instanceof User
    }

    def 'A valid basic user is returned'() {
        when:
        User user = mapBasicUser()
        then:
        user.getUserName() == 'bjensen'
    }

    @Unroll
    def 'Deserializing a simple basic user sets #fieldName field not to null'() {
        when:
        User user = mapSimpleUser()
        then:
        user[fieldName] != null

        where:
        fieldName << [
            'emails',
            'phoneNumbers',
            'ims',
            'photos',
            'addresses',
            'groups',
            'entitlements',
            'roles',
            'x509Certificates',
            'extensions'
        ]
    }

    def 'Extension gets deserialized correctly'() {
        when:
        User user = mapExtendedUser()
        then:
        user.getAllExtensions().size() == 1
        user.getAllExtensions().entrySet().first().value instanceof Extension
    }

    @Unroll
    def 'Value #fieldName is deserialized correctly'() {
        when:
        def user = mapExtendedUser()
        def extension = user.getExtension(JsonFixturesHelper.ENTERPRISE_URN)

        then:
        extension.getField(fieldName, fieldType) == fieldValue

        where:
        fieldType           |fieldName      | fieldValue
        ExtensionFieldType.STRING    | 'keyString'    | 'example'
        ExtensionFieldType.BOOLEAN   | 'keyBoolean'   | true
        ExtensionFieldType.INTEGER   | 'keyInteger'   | 123
        ExtensionFieldType.DECIMAL   | 'keyDecimal'   | 123.456
        ExtensionFieldType.BINARY    | 'keyBinary'    | [
            101,
            120,
            97,
            109,
            112,
            108,
            101] as byte[]
        ExtensionFieldType.REFERENCE | 'keyReference' | new URI('https://example.com/Users/28')
        ExtensionFieldType.DATE_TIME | 'keyDateTime'  | DateHelper.createDate(2011, 7, 1, 18, 29, 49)
    }

    def 'Extension schema registered but missing field raises exception'() {
        when:
        mapInvalidExtendedUser()
        then:
        thrown(JsonProcessingException)
    }

    def 'Extension of wrong JSON type raises exception'() {
        when:
        mapWrongFieldExtendedUser()
        then:
        thrown(JsonMappingException)
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
    private User mapInvalidExtendedUser(){
        jsonFixtures.configuredObjectMapper().readValue(jsonFixtures.jsonExtendedUserWithoutExtensionData, User)
    }
    private User mapWrongFieldExtendedUser(){
        jsonFixtures.configuredObjectMapper().readValue(jsonFixtures.jsonExtendedUserWithWrongFieldType, User)
    }
}
