package org.osiam.resources.scim

import org.skyscreamer.jsonassert.JSONAssert;

import spock.lang.Specification

import com.fasterxml.jackson.databind.ObjectMapper

class MemberRefSpec extends Specification {

    private ObjectMapper mapper = new ObjectMapper()
    
    def 'serializing member ref results in correct json'() {
        given:
        MemberRef memberRef = new MemberRef.Builder()
            .setReference('irrelevant')
            .build();
            
        when:
        def json = mapper.writeValueAsString(memberRef)
        
        then:
        JSONAssert.assertEquals('{"$ref":"irrelevant"}', json, false)
    }
    
    def 'deserializing member ref results in correct MemberRef object'() {
        given:
        String json = '{"$ref":"irrelevant"}'
        
        when:
        MemberRef memberRef = mapper.readValue(json, MemberRef)
        
        then:
        memberRef.getReference() == 'irrelevant'
    }
}
