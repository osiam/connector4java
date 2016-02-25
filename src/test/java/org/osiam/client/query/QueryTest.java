/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.client.query;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryBuilder;

public class QueryTest {

    @Test
    public void nextPage_increases_startIndex_by_count_and_returns_new_instance() {
        Query queryOriginal = new QueryBuilder()
                .startIndex(1)
                .count(10)
                .build();

        Query queryNext = queryOriginal.nextPage();

        assertThat(queryNext, is(not(sameInstance(queryOriginal))));
        assertThat(queryNext.getStartIndex(), is(11L));
    }

    @Test
    public void previousPage_decreases_startIndex_by_count_and_returns_new_instance() {
        Query queryOriginal = new QueryBuilder()
                .startIndex(21)
                .count(10)
                .build();

        Query queryNext = queryOriginal.previousPage();

        assertThat(queryNext, is(not(sameInstance(queryOriginal))));
        assertThat(queryNext.getStartIndex(), is(11L));
    }

    @Test(expected = IllegalStateException.class)
    public void previousPage_raises_exception_if_on_first_page() {
        Query queryOriginal = new QueryBuilder()
                .startIndex(1)
                .build();

        queryOriginal.previousPage();

        fail("Exception expected");
    }
}
