/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.resources.scim

import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: phil
 * Date: 5/16/13
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
class SCIMSearchResultSpec extends Specification {

    def "parametrized constructor and corresponding getter should be present"() {
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