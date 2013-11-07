package org.osiam.resources.helper

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.User
import org.osiam.resources.scim.extension.FieldType
import org.osiam.test.util.JsonFixturesHelper
import spock.lang.Specification
import spock.lang.Unroll

class UserDeserializerSpec extends Specification {

    def 'Return a User Instance'() {
        when:
        User user = JsonFixturesHelper.mapExtendedUser()
        then:
        user instanceof User
    }

    def 'A valid basic user is returned'() {
        when:
        User user = JsonFixturesHelper.mapBasicUser()
        then:
        user.getUserName() == 'bjensen'
    }

    @Unroll
    def 'Deserializing a simple basic user sets #fieldName field not to null'() {
        when:
        User user = JsonFixturesHelper.mapSimpleUser()
        then:
        user[fieldName] != null

        where:
        fieldName << ['emails',
                'phoneNumbers',
                'ims',
                'photos',
                'addresses',
                'groups',
                'entitlements',
                'roles',
                'x509Certificates',
                'extensions']
    }

    def 'Extension gets deserialized correctly'() {
        when:
        User user = JsonFixturesHelper.mapExtendedUser()
        then:
        user.getAllExtensions().size() == 1
        user.getAllExtensions().entrySet().first().value instanceof Extension
    }

    @Unroll
    def 'Value #fieldName is deserialized correctly'() {
        when:
        def user = JsonFixturesHelper.mapExtendedUser()
        def extension = user.getExtension(JsonFixturesHelper.ENTERPRISE_URN)
        then:
        extension.getField(fieldName, FieldType.STRING) == fieldValue
        where:
        fieldName        | fieldValue
        'employeeNumber' | '701984'
        'organization'   | 'Universal Studios'
        'department'     | 'Tour Operations'
    }

    def 'Extension schema registered but missing field raises exception'() {
        when:
        JsonFixturesHelper.mapInvalidExtendedUser()
        then:
        thrown(JsonProcessingException)
    }

    def 'Extension of wrong JSON type raises exception'() {
        when:
        JsonFixturesHelper.mapWrongFieldExtendedUser()
        then:
        thrown(JsonMappingException)
    }

}
