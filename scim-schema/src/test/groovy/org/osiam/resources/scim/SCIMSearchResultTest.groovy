package org.osiam.resources.scim

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: phil
 * Date: 5/16/13
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
class SCIMSearchResultTest extends Specification {

    def "parametrized constructor and corresponding getter should be present"(){
        given:
        def result = ["searchResult"] as List
        def schema = "schema1"
        def schemas = [schema] as Set

        when:
        def a = new SCIMSearchResult(result, 2342, 100, 1, schema)

        then:
        a.resources == result
        a.totalResults == 2342
        a.itemsPerPage == 100
        a.startIndex == 1
        a.schemas == schemas
    }

    def "parametrized constructor with schema collection should be present"(){
        given:
        def result = ["searchResult"] as List
        def schemas = ["schema1"] as Set

        when:
        def a = new SCIMSearchResult(result, 2342, 100, 1, schemas)

        then:
        a.resources == result
        a.totalResults == 2342
        a.itemsPerPage == 100
        a.startIndex == 1
        a.schemas == schemas
    }

    def "empty constructor should be present for jackson"() {
        when:
        def result = new SCIMSearchResult()

        then:
        result
    }
}