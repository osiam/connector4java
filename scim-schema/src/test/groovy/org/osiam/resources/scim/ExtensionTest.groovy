package org.osiam.resources.scim

import org.codehaus.groovy.classgen.Verifier.DefaultArgsAction;
import org.codehaus.groovy.transform.sc.StaticCompilationMetadataKeys;

import spock.lang.Specification
import spock.lang.Unroll;


class ExtensionTest extends Specification {

    static def FIELD = 'foo'
    static def VALUE = 'bar'
    
    static def FIELD_INJECTED = 'injected'
    static def VALUE_INJECTED = 'iwasinjected'

    def 'Query for existing field returns value'(){
        given:
            def extension = new Extension([(FIELD):VALUE])
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
    
    @Unroll
    def 'Setting a field with a #testCase name raises exception'(){
        given:
            def extension = new Extension()
        when:
            extension.setField(fieldName, VALUE)
        then:
            thrown(expectedException)
            
        where:
            testCase  | fieldName      | expectedException
            'null'    | null           | IllegalArgumentException
            'invalid' | FIELD_INJECTED | IllegalArgumentException
    }

    def 'getAllFields returns a map of all the fields'(){
        given:
            def extension = new Extension([(FIELD):VALUE, (VALUE):FIELD])
        when:
            def result = extension.allFields
        then:
            result.size() == 2
            result[FIELD] == VALUE
            result[VALUE] == FIELD
    }

    def 'getAllFields returns a immutable map'(){
        given:
            def extension = new Extension([(FIELD):VALUE])
            def result = extension.getAllFields()
        when:
            result[FIELD] = FIELD
        then:
            thrown(UnsupportedOperationException)
    }
    
    def 'the constructor creates a copy of its parameter' () {
        given:
            def map = [(FIELD):VALUE, (VALUE):FIELD]
            def extension = new Extension(map)
        when:
            map[FIELD_INJECTED] = VALUE_INJECTED
            
        then:
            !extension.fields.containsKey(FIELD_INJECTED)
    }
}
