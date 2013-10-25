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

import spock.lang.Ignore;
import spock.lang.Specification
import spock.lang.Unroll


class UserDeserializerSpec extends Specification {

    private static final SimpleTemplateEngine TEMPLATE_ENGINE = new SimpleTemplateEngine()
    private static final Template TEMPLATE = TEMPLATE_ENGINE.createTemplate(JSON_SIMPLE_USER)
    
    private static final String ENTERPRISE_URN = 'urn:scim:schemas:extension:enterprise:2.0:User'
    
    private static final String JSON_SIMPLE_USER = '{"id":"a4bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4","meta":{"created":"2011-08-01T20:29:49.000+02:00","lastModified":"2011-08-01T20:29:49.000+02:00","location":"https://example.com/v1/Users/2819c223...","resourceType":"User"},"schemas":[$schemasMore"urn:scim:schemas:core:1.0"],"externalId":"bjensen","userName":"bjensen","name":{"formatted":"Ms. Barbara J Jensen III","familyName":"Jensen","givenName":"Barbara"},"displayName":"BarbaraJ.","nickName":"Barbara","title":"Dr.","locale":"de"$dataMore}'
    private static final String JSON_PARTIAL_COLLECTIONS = ',"emails":[{"value":"bjensen@example.com","type":"work"}],"phoneNumbers":[{"value":"555-555-8377","type":"work"}],"addresses":[{"type":"work","streetAddress":"example street 42","locality":"Germany","region":"Germany","postalCode":"11111","country":"Germany"}]'
    private static final String JSON_PARTIAL_EXTENSION = ',"urn:scim:schemas:extension:enterprise:2.0:User":{"organization":"Universal Studios","department":"Tour Operations","employeeNumber":"701984"}'
    private static final String JSON_PARTIAL_WRONG_TYPE_EXTENSION = ',"urn:scim:schemas:extension:enterprise:2.0:User": [10]'
    private static final String JSON_PARTIAL_ENTERPRISE_URN = '"' + ENTERPRISE_URN + '",'

    def 'Return an User Instance'(){
        when:
            User user = mapExtendedUser()
        then:
            user instanceof User
    }

    def 'A valid basic user is returned'(){
        when:
            User user = mapBasicUser()
        then:
            user.getUserName() == 'bjensen'
    }

    @Unroll
    def 'Deserializing a simple basic user sets #fieldName field not to null'(){
        when:
            User user = mapSimpleBasicUser()
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
            User user = mapExtendedUser()
        then:
            user.getAllExtensions().size() == 1
            user.getAllExtensions().entrySet().first().value instanceof Extension
    }

    @Unroll
    def 'Value #fieldName is deserialized correctly'(){
        when:
            def user = mapExtendedUser()
            def extension = user.getExtension(ENTERPRISE_URN)
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
            mapInvalidExtendedUser()
        then:
            thrown(JsonProcessingException)
    }

    def 'Extension of wrong JSON type raises exception'(){
        when:
            mapWrongFieldExtendedUser()
        then:
            thrown(JsonMappingException)
    }

    def configuredObjectMapper(){
        ObjectMapper mapper = new ObjectMapper()
        SimpleModule testModule = new SimpleModule('MyModule', new Version(1, 0, 0, null))
                .addDeserializer(User, new UserDeserializer(User))
        mapper.registerModule(testModule)
        return mapper
    }

    def mapBasicUser(){
        def jsonString = TEMPLATE.make([schemasMore:'', dataMore:JSON_PARTIAL_COLLECTIONS]).toString()
        configuredObjectMapper().readValue(jsonString, User)
    }
    def mapSimpleBasicUser(){
        def jsonString = TEMPLATE.make([schemasMore:'', dataMore:'']).toString()
        configuredObjectMapper().readValue(jsonString, User)
    }
    def mapExtendedUser(){
        def jsonString = TEMPLATE.make([schemasMore:JSON_PARTIAL_ENTERPRISE_URN, dataMore:JSON_PARTIAL_COLLECTIONS + JSON_PARTIAL_EXTENSION]).toString()
        configuredObjectMapper().readValue(jsonString, User)
    }
    def mapInvalidExtendedUser(){
        def jsonString = TEMPLATE.make([schemasMore:JSON_PARTIAL_ENTERPRISE_URN, dataMore:JSON_PARTIAL_COLLECTIONS]).toString()
        configuredObjectMapper().readValue(jsonString, User)
    }
    def mapWrongFieldExtendedUser(){
        def jsonString = TEMPLATE.make([schemasMore:JSON_PARTIAL_ENTERPRISE_URN, dataMore:JSON_PARTIAL_COLLECTIONS + JSON_PARTIAL_WRONG_TYPE_EXTENSION]).toString()
        configuredObjectMapper().readValue(jsonString, User)
    }
}
