package org.osiam.resources.helper

import org.codehaus.jackson.JsonProcessingException
import org.codehaus.jackson.Version
import org.codehaus.jackson.map.JsonMappingException
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.map.module.SimpleModule
import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.User
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll


class UserDeserializerSpec extends Specification {

    private static final String BASIC_FILE_PATH = 'src/test/resources/basicUser.json'
    private static final String EXTENDED_FILE_PATH = 'src/test/resources/extendedUser.json'
    private static final String INVALID_FILE_PATH = 'src/test/resources/invalidExtendedUser.json'
    private static final String WRONG_FIELD_FILE_PATH = 'src/test/resources/wrongFieldExtendedUser.json'
    private static final String ENTERPRISE_URN = 'urn:scim:schemas:extension:enterprise:2.0:User'

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

    def 'Extension gets deserialized correctly'(){
        when:
            User user = mapExtendedUser()
        then:
            user.getAllExtensions().size() == 1
            user.getAllExtensions().entrySet().first().value instanceof Extension
    }

    @Ignore
    def 'Extended User is Serialized'(){
        given:
            def user = mapExtendedUser();
            def mapper = new ObjectMapper()
        when:
           def result = mapper.writeValueAsString(user)
        then:
            thrown(NullPointerException)

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
       configuredObjectMapper().readValue(new File(BASIC_FILE_PATH), User)
    }
    def mapExtendedUser(){
        configuredObjectMapper().readValue(new File(EXTENDED_FILE_PATH), User)
    }
    def mapInvalidExtendedUser(){
        configuredObjectMapper().readValue(new File(INVALID_FILE_PATH), User)
    }
    def mapWrongFieldExtendedUser(){
        configuredObjectMapper().readValue(new File(WRONG_FIELD_FILE_PATH), User)
    }
}
