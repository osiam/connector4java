package org.osiam.client.query;
/*
 * for licensing see the file license.txt.
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Query {
    static final private int DEFAULT_COUNT = 100;
    static final private int DEFAULT_INDEX = 0;
    static final private Pattern INDEX_PATTERN = Pattern.compile("startIndex=(\\d+)&?");
    static final private Pattern COUNT_PATTERN = Pattern.compile("count=(\\d+)&?");
    final private String queryString;

    private Matcher indexMatcher;
    private Matcher countMatcher;

    public Query(String queryString) {
        this.queryString = queryString;
        indexMatcher = INDEX_PATTERN.matcher(queryString);
        countMatcher = COUNT_PATTERN.matcher(queryString);
    }

    /**
     * Returns the number of items per page this query is configured for. If no explicit number was given, the default
     * number of items per page is returned, which is 100.
     *
     * @return The number of Items this Query is configured for.
     */
    public int getCount() {
        if (queryStringContainsCount()) {
            return Integer.parseInt(countMatcher.group(1));
        }
        return DEFAULT_COUNT;
    }

    /**
     * Returns the startIndex ot this query. If no startIndex was set, it returns the default, which is 0.
     *
     * @return The startIndex of this query.
     */
    public int getStartIndex() {
        if (queryStringContainsIndex()) {
            return Integer.parseInt(indexMatcher.group(1));
        }
        return DEFAULT_INDEX;
    }

    /**
     * Create a new query that is moved forward in the result set by one page.
     *
     * @return A new query paged forward by one.
     */
    public Query nextPage() {
        String nextIndex = "startIndex=" + (getCount() + getStartIndex());
        if (queryStringContainsIndex()) {
            return new Query(indexMatcher.replaceFirst(nextIndex));
        }
        return new Query(queryString + "&" + nextIndex);
    }

    /**
     * Create a new query that is moved backward the result set by one page.
     *
     * @return A new query paged backward by one.
     */
    public Query previousPage() {
        int newIndex = getStartIndex() - getCount();
        if (newIndex < 0) {
            throw new IllegalStateException("Negative startIndex is not possible.");
        }
        String prevIndex = "startIndex=" + newIndex;
        return new Query(indexMatcher.replaceFirst("" + prevIndex));
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Query query = (Query) other;

        return queryString.equals(query.queryString);

    }

    @Override
    public int hashCode() {
        return queryString.hashCode();
    }

    /**
     * @return The query as a String that can be used in a web request.
     */
    public String toString() {
        return queryString;
    }

    private boolean queryStringContainsIndex() {
        return indexMatcher.find(0);
    }

    private boolean queryStringContainsCount() {
        return countMatcher.find(0);
    }
}