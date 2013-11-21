package org.osiam.resources.scim

import spock.lang.Specification

class SchemaRegressionSpec extends Specification {

    def 'REG-BT-13'() {
        given:
        def extension1Urn = "urn:org.osiam:schemas:test:1.0:Test1"
        def extension1 = new Extension(extension1Urn)
        def extension2Urn = "urn:org.osiam:schemas:test:1.0:Test2"
        def extension2 = new Extension(extension2Urn)
        def coreSchemaUrn = Constants.CORE_SCHEMA

        when:
        def userWithExtensions = new User.Builder("test2")
                .addExtension(extension1)
                .addExtension(extension2)
                .build()

        def userWithoutExtensions = new User.Builder("test2")
                .build()
        then:
        userWithoutExtensions.schemas.contains(coreSchemaUrn)
        !userWithoutExtensions.schemas.contains(extension1Urn)
        !userWithoutExtensions.schemas.contains(extension2Urn)
    }
}
