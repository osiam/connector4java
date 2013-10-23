package org.osiam.resources.scim

import spock.lang.Specification

class SchemaRegressionTest extends Specification {
    
    def 'REG-BT-13'() {
        given:
            def extension1Urn = "urn:org.osiam:schemas:test:1.0:Test1"
            def extension1 = new Extension([:])
            def extension2Urn = "urn:org.osiam:schemas:test:1.0:Test2"
            def extension2 = new Extension([:])
            def coreSchemaUrn = Constants.CORE_SCHEMA
            
        when:
            def userWithExtensions = new User.Builder("test2")
                    .addExtension(extension1Urn, extension1)
                    .addExtension(extension2Urn, extension2)
                    .build()
        
            def userWithoutExtensions = new User.Builder("test2")
                    .build()
        then:
            userWithoutExtensions.schemas.contains(coreSchemaUrn)
            !userWithoutExtensions.schemas.contains(extension1Urn)
            !userWithoutExtensions.schemas.contains(extension2Urn)
    }
}
