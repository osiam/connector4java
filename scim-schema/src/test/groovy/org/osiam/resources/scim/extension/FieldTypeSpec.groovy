package org.osiam.resources.scim.extension

import spock.lang.Specification
import spock.lang.Unroll

class FieldTypeSpec extends Specification {

    @Unroll
    def 'getName on FieldType.#givenTypeInstance returns #expectedTypeName'() {
        when:
        def typeName = givenTypeInstance.getName()
        
        then:
        typeName == expectedTypeName

        where:
        givenTypeInstance    | expectedTypeName
        FieldType.STRING     | 'STRING'
        FieldType.INTEGER    | 'INTEGER'
        FieldType.DECIMAL    | 'DECIMAL'
        FieldType.BOOLEAN    | 'BOOLEAN'
        FieldType.DATE_TIME  | 'DATE_TIME'
        FieldType.BINARY     | 'BINARY'
        FieldType.REFERENCE  | 'REFERENCE'
    }
    
    @Unroll
    def 'fromString on FieldType.#givenTypeInstance returns the correctly typed value'(){
        when:
        def output = givenTypeInstance.fromString(givenInputValue)
        
        then:
        output == expectedOutputValue
        
        where:
        givenTypeInstance    | givenInputValue                | expectedOutputValue
        FieldType.STRING     | 'example'                      | 'example'
        FieldType.INTEGER    | '123'                          | 123G
        FieldType.DECIMAL    | '12.3'                         | 12.3G
        FieldType.BOOLEAN    | 'true'                         | true
        FieldType.DATE_TIME  | '2011-08-01T18:29:49.000Z'     | createDate(2011, 7, 1, 18, 29, 49)
        FieldType.BINARY     | 'ZXhhbXBsZQ=='                 | [101, 120, 97, 109, 112, 108, 101] as byte[]                    
        FieldType.REFERENCE  | 'https://example.com/Users/28' | new URI('https://example.com/Users/28')
    }
    
    @Unroll
    def 'fromString on FieldType.#givenTypeInstance with illegal value raises exception'(){
        when:
        givenTypeInstance.fromString(givenInputValue)
        
        then:
        thrown(IllegalArgumentException)
        
        where:
        givenTypeInstance    | givenInputValue                
        FieldType.INTEGER    | 'illegal'                          
        FieldType.DECIMAL    | 'illegal'                         
        FieldType.DATE_TIME  | 'illegal'         
        FieldType.BINARY     | '!@#$%^&*()_+'                 
        FieldType.REFERENCE  | '!@#$%^&*()_+' 
    }

    @Unroll
    def 'fromString on FieldType.#givenTypeInstance with null value raises exception'(){
        when:
        givenTypeInstance.fromString(givenInputValue)
        
        then:
        thrown(IllegalArgumentException)
        
        where:
        givenTypeInstance    | givenInputValue
        FieldType.STRING     | null
        FieldType.INTEGER    | null       
        FieldType.DECIMAL    | null
        FieldType.BOOLEAN    | null
        FieldType.DATE_TIME  | null
        FieldType.BINARY     | null
        FieldType.REFERENCE  | null
    }
    
    @Unroll
    def 'toString on FieldType.#givenTypeInstance with null value raises exception'(){
        when:
        givenTypeInstance.toString(givenInputValue)
        
        then:
        thrown(IllegalArgumentException)
        
        where:
        givenTypeInstance    | givenInputValue
        FieldType.STRING     | null
        FieldType.INTEGER    | null
        FieldType.DECIMAL    | null
        FieldType.BOOLEAN    | null
        FieldType.DATE_TIME  | null
        FieldType.BINARY     | null
        FieldType.REFERENCE  | null
    }
    
    @Unroll
    def 'toString on FieldType.#givenTypeInstance returns the correct String value'(){
        
        when:
        def output = givenTypeInstance.toString(givenInputValue)
        
        then:
        output == expectedOutputValue
        
        where:
        givenTypeInstance    | givenInputValue                              | expectedOutputValue
        FieldType.STRING     | 'example'                                    | 'example'
        FieldType.INTEGER    | 123G                                         | '123'
        FieldType.DECIMAL    | 12.3G                                        | '12.3'
        FieldType.BOOLEAN    | true                                         | 'true'
        FieldType.DATE_TIME  | createDate(2008, 0, 23, 4, 56, 22)           | '2008-01-23T04:56:22.000Z'
        FieldType.BINARY     | [101, 120, 97, 109, 112, 108, 101] as byte[] | 'ZXhhbXBsZQ==' 
        FieldType.REFERENCE  | new URI('https://example.com/Users/28')      | 'https://example.com/Users/28' 
    }
    
    @Unroll
    def 'retrieving FieldType instance via static valueOf with "#typeName" returns FieldType.#expectedTypeInstance'() {
        when:
        def typeInstance = FieldType.valueOf(typeName)

        then:
        typeInstance == expectedTypeInstance

        where:
        typeName    | expectedTypeInstance
        'STRING'    | FieldType.STRING
        'INTEGER'   | FieldType.INTEGER
        'DECIMAL'   | FieldType.DECIMAL
        'BOOLEAN'   | FieldType.BOOLEAN
        'DATE_TIME' | FieldType.DATE_TIME
        'BINARY'    | FieldType.BINARY
        'REFERENCE' | FieldType.REFERENCE
    }

    def 'retrieving FieldType instance via static valueOf with unknown type name raises exception'() {
        given:
        def unknownTypeName = 'unknown'

        when:
        FieldType.valueOf(unknownTypeName)

        then:
        thrown(IllegalArgumentException)
    }
    
    private def createDate(int year, int month, int date, int hourOfDay, int minute,
                          int second) {
        Calendar calendar = Calendar.getInstance()
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.setTimeZone(TimeZone.getTimeZone(TimeZone.GMT_ID))
        calendar.set(year, month, date, hourOfDay, minute, second)
        calendar.getTime()
    }
}
