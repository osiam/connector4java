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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Builder for {@link Query} objects.
 */
public class QueryBuilder {
    public static final long DEFAULT_START_INDEX = 1;
    public static final int DEFAULT_COUNT = 100;
    public static final int MAX_COUNT = 100;

    String attributes;

    String filter;

    String sortBy;
    String sortOrder;

    long startIndex = DEFAULT_START_INDEX;
    int count = DEFAULT_COUNT;

    /**
     * Constructs a new empty {@link QueryBuilder}.
     */
    public QueryBuilder() {
    }

    /**
     * Constructs a new {@link QueryBuilder} with values copied from the given
     * {@link Query}.
     */
    public QueryBuilder(Query original) {
        attributes = original.getAttributes();
        filter = original.getFilter();
        sortBy = original.getSortBy();
        sortOrder = original.getSortOrder();
        startIndex = original.getStartIndex();
        count = original.getCount();
    }

    /**
     * Creates a new {@link Query} using this builder's values.
     *
     * @return The query built by this {@link QueryBuilder}
     */
    public Query build() {
        return new Query(this);
    }

    /**
     * Sets the attributes of the resources to return as a comma-separated list,
     * e.g <code>userName, displayName</code>.
     *
     * @param attributes
     *        list of attributes to return
     *
     * @see http://tools.ietf.org/html/draft-ietf-scim-api-04#section-3.7
     */
    public QueryBuilder attributes(String attributes) {
        this.attributes = attributes;
        return this;
    }

    /**
     * Filter the resulting resources by the given filter string.
     *
     * @param filter
     *        the filter string
     * @see http://tools.ietf.org/html/draft-ietf-scim-api-04#section-3.2.2.2
     */
    public QueryBuilder filter(String filter) {
        this.filter = filter;
        return this;
    }

    /**
     * Sort the resulting resources ascending by the given attribute.
     *
     * @param sortByAttribute
     *        the attribute to sort the resulting resources by
     * @see http://tools.ietf.org/html/draft-ietf-scim-api-04#section-3.2.2.3
     */
    public QueryBuilder ascending(String sortByAttribute) {
        sortBy = sortByAttribute;
        sortOrder = "ascending";
        return this;
    }

    /**
     * Sort the resulting resources descending by the given attribute.
     *
     * @param sortByAttribute
     *        the attribute to sort the resulting resources by
     * @see http://tools.ietf.org/html/draft-ietf-scim-api-04#section-3.2.2.3
     */
    public QueryBuilder descending(String sortByAttribute) {
        sortBy = sortByAttribute;
        sortOrder = "descending";
        return this;
    }

    /**
     * Sets the index (1-based) of the first resource in the list of returned
     * resources.
     *
     * @param startIndex
     *        the (1-based) index of the first resource
     *
     * @see http://tools.ietf.org/html/draft-ietf-scim-api-04#section-3.2.2.4
     */
    public QueryBuilder startIndex(long startIndex) {
        this.startIndex = startIndex < 1 ? DEFAULT_START_INDEX : startIndex;
        return this;
    }

    /**
     * Sets the number of returned resources per page.
     *
     * @param count
     *        the number of returned resources
     * @see http://tools.ietf.org/html/draft-ietf-scim-api-04#section-3.2.2.4
     */
    public QueryBuilder count(int count) {
        this.count = count < 1 ? DEFAULT_COUNT : count;
        return this;
    }

    /**
     * Formats a given {@link DateTime} to the SCIM needed string form with the
     * pattern "yyyy-MM-dd'T'HH:mm:ss.SSS"
     *
     * @param dateTime
     *        dateTime to be converted
     * @return dateTime as scim conform String
     */
    public static String getScimConformFormatedDateTime(DateTime dateTime) {
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return dateFormat.print(dateTime);
    }
}
