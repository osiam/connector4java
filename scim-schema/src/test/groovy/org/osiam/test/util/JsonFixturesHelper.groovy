package org.osiam.test.util

import groovy.text.SimpleTemplateEngine
import groovy.text.Template

import org.codehaus.jackson.Version
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.map.module.SimpleModule
import org.osiam.resources.helper.UserDeserializer
import org.osiam.resources.scim.User

class JsonFixturesHelper {
    
    private static final SimpleTemplateEngine TEMPLATE_ENGINE = new SimpleTemplateEngine()
    private static final Template TEMPLATE = TEMPLATE_ENGINE.createTemplate(TPL_JSON_SIMPLE_USER)
    private static final String TPL_JSON_SIMPLE_USER = '{"id":"a4bbe688-4b1e-4e4e-80e7-e5ba5c4d6db4","meta":{"created":"2011-08-01T20:29:49.000+02:00","lastModified":"2011-08-01T20:29:49.000+02:00","location":"https://example.com/v1/Users/2819c223...","resourceType":"User"},"schemas":[$schemasMore"urn:scim:schemas:core:1.0"],"externalId":"bjensen","userName":"bjensen","name":{"formatted":"Ms. Barbara J Jensen III","familyName":"Jensen","givenName":"Barbara"},"displayName":"BarbaraJ.","nickName":"Barbara","title":"Dr.","locale":"de"$dataMore}'
    private static final String TPL_JSON_PARTIAL_COLLECTIONS = ',"emails":[{"value":"bjensen@example.com","type":"work"}],"phoneNumbers":[{"value":"555-555-8377","type":"work"}],"addresses":[{"type":"work","streetAddress":"example street 42","locality":"Germany","region":"Germany","postalCode":"11111","country":"Germany"}]'
    private static final String TPL_JSON_PARTIAL_EXTENSION = ',"urn:scim:schemas:extension:enterprise:2.0:User":{"organization":"Universal Studios","department":"Tour Operations","employeeNumber":"701984"}'
    private static final String TPL_JSON_PARTIAL_WRONG_TYPE_EXTENSION = ',"urn:scim:schemas:extension:enterprise:2.0:User": [10]'
    private static final String TPL_JSON_PARTIAL_ENTERPRISE_URN = '"' + ENTERPRISE_URN + '",'
    
    public static final String ENTERPRISE_URN = 'urn:scim:schemas:extension:enterprise:2.0:User'
    public static final String JSON_SIMPLE_USER = TEMPLATE.make([schemasMore:'', dataMore:'']).toString()
    public static final String JSON_BASIC_USER = TEMPLATE.make([schemasMore:'', dataMore:TPL_JSON_PARTIAL_COLLECTIONS]).toString()
    public static final String JSON_EXTENDED_USER = TEMPLATE.make([schemasMore:TPL_JSON_PARTIAL_ENTERPRISE_URN, dataMore:TPL_JSON_PARTIAL_COLLECTIONS + TPL_JSON_PARTIAL_EXTENSION]).toString()
    public static final String JSON_EXTENDED_USER_WITHOUT_EXTENSION_DATA = TEMPLATE.make([schemasMore:TPL_JSON_PARTIAL_ENTERPRISE_URN, dataMore:TPL_JSON_PARTIAL_COLLECTIONS]).toString()
    public static final String JSON_EXTENDED_USER_WITH_WRONG_FIELD_TYPE =TEMPLATE.make([schemasMore:TPL_JSON_PARTIAL_ENTERPRISE_URN, dataMore:TPL_JSON_PARTIAL_COLLECTIONS + TPL_JSON_PARTIAL_WRONG_TYPE_EXTENSION]).toString()
    
    private static def configuredObjectMapper(){
        ObjectMapper mapper = new ObjectMapper()
        SimpleModule testModule = new SimpleModule('MyModule', new Version(1, 0, 0, null))
                .addDeserializer(User, new UserDeserializer(User))
        mapper.registerModule(testModule)
        return mapper
    }
    
    public static def mapBasicUser(){
        configuredObjectMapper().readValue(JSON_BASIC_USER, User)
    }
    public static def mapSimpleUser(){
        configuredObjectMapper().readValue(JSON_SIMPLE_USER, User)
    }
    public static def mapExtendedUser(){
        configuredObjectMapper().readValue(JSON_EXTENDED_USER, User)
    }
    public static def mapInvalidExtendedUser(){
        configuredObjectMapper().readValue(JSON_EXTENDED_USER_WITHOUT_EXTENSION_DATA, User)
    }
    public static def mapWrongFieldExtendedUser(){
        configuredObjectMapper().readValue(JSON_EXTENDED_USER_WITH_WRONG_FIELD_TYPE, User)
    }
}
