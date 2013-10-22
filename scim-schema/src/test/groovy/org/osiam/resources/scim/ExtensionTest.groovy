package org.osiam.resources.scim

import spock.lang.Specification


class ExtensionTest extends Specification {

    static def FIELD = 'foo'
    static def VALUE = 'bar'

    def 'Query for existing field returns value'(){
        given:
            def extension = new Extension(fields:[(FIELD):VALUE])
        expect:
            extension.getField(FIELD) == VALUE
    }

    def 'Query for non-existing field raises NoSuchElementException'(){
        given:
            def extension = new Extension()
        when:
            extension.getField(FIELD)
        then:
            thrown(NoSuchElementException)

    }

    def 'Query for Null raises IllegalArgumentException'(){
        given:
            def extension = new Extension()
        when:
            extension.getField(null)
        then:
            thrown(IllegalArgumentException)

    }

    def 'Query for an empty field name raises IllegalArgumentException'(){
        given:
            def extension = new Extension()
        when:
            extension.getField('')
        then:
            thrown(IllegalArgumentException)
    }

    def 'Setting a valid element adds it to fields'(){
        given:
            def extension = new Extension()
        when:
            extension.setField(FIELD,VALUE)
        then:
            extension.fields[FIELD] == VALUE
    }

    def 'Setting a field to an empty string raises IllegalArgumentException'(){
        given:
            def extension = new Extension()
        when:
            extension.setField('', VALUE)
        then:
            thrown(IllegalArgumentException)
    }

    def 'Setting a field to null raises IllegalArgumentException'(){
        given:
            def extension = new Extension()
        when:
            extension.setField(null, VALUE)
        then:
            thrown(IllegalArgumentException)
    }

    def 'getAllFields returns a map of all the fields'(){
        given:
            def extension = new Extension(fields: [(FIELD):VALUE, (VALUE):FIELD])
        when:
            def result = extension.allFields
        then:
            result.size() == 2
            result[FIELD] == VALUE
            result[VALUE] == FIELD
    }

    def 'getAllFields returns a immutable map'(){
        given:
            def extension = new Extension(fields: [(FIELD):VALUE])
            def result = extension.getAllFields()
        when:
            result[FIELD] = FIELD
        then:
            thrown(UnsupportedOperationException)
    }
}
