package org.osiam.test.util

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import groovy.text.SimpleTemplateEngine
import groovy.text.Template

import org.osiam.resources.helper.UserDeserializer
import org.osiam.resources.scim.User

class JsonFixturesHelper {

    public final static String ENTERPRISE_URN = 'urn:scim:schemas:extension:enterprise:2.0:User'

    private final String tplJsonSimpleUser = getClass().getResourceAsStream('TPL_JSON_SIMPLE_USER.json').text
    private final String tplJsonPartialCollections = getClass().getResourceAsStream('TPL_JSON_PARTIAL_COLLECTIONS.json').text
    private final String tplJsonPartialExtension = getClass().getResourceAsStream('TPL_JSON_PARTIAL_EXTENSION.json').text
    private final String tplJsonPartialWrongTypeExtension = getClass().getResourceAsStream('TPL_JSON_PARTIAL_WRONG_TYPE_EXTENSION.json').text
    private final String tplJsonPartialEnterpriseUrn = ',"' + ENTERPRISE_URN + '"'

    private final SimpleTemplateEngine templateEngine = new SimpleTemplateEngine()
    private final Template template = templateEngine.createTemplate(tplJsonSimpleUser)

    public final String jsonSimpleUser = template.make([schemasMore:'', dataMore:'']).toString()
    public final String jsonBasicUser = template.make([schemasMore:'', dataMore:tplJsonPartialCollections]).toString()
    public final String jsonExtendedUser = template.make([schemasMore:tplJsonPartialEnterpriseUrn, dataMore:tplJsonPartialCollections + tplJsonPartialExtension]).toString()
    public final String jsonExtendedUserWithoutExtensionData = template.make([schemasMore:tplJsonPartialEnterpriseUrn, dataMore:tplJsonPartialCollections]).toString()
    public final String jsonExtendedUserWithWrongFieldType = template.make([schemasMore:tplJsonPartialEnterpriseUrn, dataMore:tplJsonPartialCollections + tplJsonPartialWrongTypeExtension]).toString()

    public ObjectMapper configuredObjectMapper() {
        ObjectMapper mapper = new ObjectMapper()
        SimpleModule testModule = new SimpleModule('MyModule', new Version(1, 0, 0, null))
                .addDeserializer(User, new UserDeserializer(User))
        mapper.registerModule(testModule)
        return mapper
    }
}
