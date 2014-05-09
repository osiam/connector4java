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

package org.osiam.client.query;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class StringQueryBuilderTest {

    @Test
    public void stringQueryBuilder_works_as_aspected() {
        StringQueryBuilder queryBuilder = new StringQueryBuilder()
                .setFilter("userName eq  \"marissa\"")
                .setSortBy("userName")
                .setCount(50)
                .setSortOrder("ascending")
                .setStartIndex(3);
        String query;

        query = queryBuilder.build();
        String excpectedtQuery = "filter=userName+eq++%22marissa%22&sortBy=userName&sortOrder=ascending&count=50&startIndex=3";
        assertThat(query, is(excpectedtQuery));
    }

    public void stringQueryBuilder_works_as_aspected_while_two_attributes_are_set() {
        StringQueryBuilder queryBuilder = new StringQueryBuilder()
                .setFilter("userName eq  \"marissa\"")
                .setCount(50);
        String query = queryBuilder.build();

        String excpectedtQuery = "&filter=userName+eq++%22marissa%22&count=50";
        assertThat(query, is(excpectedtQuery));
    }
}
