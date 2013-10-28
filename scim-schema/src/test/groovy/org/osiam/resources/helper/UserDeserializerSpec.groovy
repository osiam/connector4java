package org.osiam.resources.helper

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import groovy.text.SimpleTemplateEngine
import groovy.text.Template

import org.codehaus.jackson.JsonProcessingException
import org.codehaus.jackson.Version
import org.codehaus.jackson.map.JsonMappingException
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.map.module.SimpleModule
import org.osiam.resources.scim.Address;
import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.resources.scim.User
import org.osiam.test.util.JsonFixturesHelper;

import spock.lang.Ignore;
import spock.lang.Specification
import spock.lang.Unroll


class UserDeserializerSpec extends Specification {

    def 'Return an User Instance'(){
        when:
            User user = JsonFixturesHelper.mapExtendedUser()
        then:
            user instanceof User
    }

    def 'A valid basic user is returned'(){
        when:
            User user = JsonFixturesHelper.mapBasicUser()
        then:
            user.getUserName() == 'bjensen'
    }

    @Unroll
    def 'Deserializing a simple basic user sets #fieldName field not to null'(){
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

    def 'Extension gets deserialized correctly'(){
        when:
            User user = JsonFixturesHelper.mapExtendedUser()
        then:
            user.getAllExtensions().size() == 1
            user.getAllExtensions().entrySet().first().value instanceof Extension
    }

    @Unroll
    def 'Value #fieldName is deserialized correctly'(){
        when:
            def user = JsonFixturesHelper.mapExtendedUser()
            def extension = user.getExtension(JsonFixturesHelper.ENTERPRISE_URN)
        then:
            extension.getField(fieldName) == fieldValue
        where:
            fieldName        | fieldValue
            'employeeNumber' | '701984'
            'organization'   | 'Universal Studios'
            'department'     | 'Tour Operations'
    }

    def 'Extension schema registered but missing field raises exception'(){
        when:
            JsonFixturesHelper.mapInvalidExtendedUser()
        then:
            thrown(JsonProcessingException)
    }

    def 'Extension of wrong JSON type raises exception'(){
        when:
            JsonFixturesHelper.mapWrongFieldExtendedUser()
        then:
            thrown(JsonMappingException)
    }

}
