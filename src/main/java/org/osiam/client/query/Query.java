package org.osiam.client.query;
/*
 * for licensing see the file license.txt.
 */

import org.apache.commons.io.Charsets;
import org.osiam.client.exception.InvalidAttributeException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a query as it is run against the OSIAM service.
 */
public class Query {
    private static final int DEFAULT_COUNT = 100;
    private static final int DEFAULT_INDEX = 0;
    private static final Pattern INDEX_PATTERN = Pattern.compile("startIndex=(\\d+)&?");
    private static final Pattern COUNT_PATTERN = Pattern.compile("count=(\\d+)&?");

    private final String queryString;

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

    /**
     * The Builder is used to construct instances of the {@link Query}
     */
    public static final class Builder {

        private static final int DEFAULT_START_INDEX = 0;
        private static final int DEFAULT_COUNT_PER_PAGE = 100;
        @SuppressWarnings("rawtypes")
	private Class clazz;
        private StringBuilder filter;
        private String sortBy;
        private SortOrder sortOrder;
        private int startIndex = DEFAULT_START_INDEX;
        private int countPerPage = DEFAULT_COUNT_PER_PAGE;

        /**
         * The Constructor of the QueryBuilder
         *
         * @param clazz The class of Resources to query for.
         */
        public Builder(@SuppressWarnings("rawtypes") Class clazz) {
            filter = new StringBuilder();
            this.clazz = clazz;
        }

        /**
         * Add a filter on the given Attribute.
         *
         * @param attributeName The name of the attribute to filter on.
         * @return A {@link Filter} to specify the filtering criteria
         * @throws org.osiam.client.exception.InvalidAttributeException if the given attribute is not valid for a query
         */
        public Filter filter(String attributeName) {
            return query(attributeName);
        }

        /**
         * Add an 'logical and' operation to the filter with another attribute to filter on.
         *
         * @param attributeName The name of the attribute to filter the and clause on.
         * @return A {@link Filter} to specify the filtering criteria
         * @throws org.osiam.client.exception.InvalidAttributeException if the given attribute is not valid for a query
         */
        public Filter and(String attributeName) {
            filter.append(" and ");
            return query(attributeName);
        }

        /**
         * Adds the query of the given Builder into ( and ) to the filter
         *
         * @param innerFilter the inner filter
         * @return The Builder with the inner filter added.
         */
        public Builder and(Builder innerFilter) {
            filter.append(" and (").append(innerFilter.filter).append(")");
            return this;
        }

        /**
         * Add an 'logical or' operation to the filter with another attribute to filter on.
         *
         * @param attributeName The name of the attribute to filter the or clause on.
         * @return A {@link Filter} to specify the filtering criteria
         * @throws org.osiam.client.exception.InvalidAttributeException if the given attribute is not valid for a query
         */
        public Filter or(String attributeName) {
            filter.append(" or ");
            return query(attributeName);
        }

        /**
         * Adds the query of the given Builder into ( and ) to the filter
         *
         * @param innerFilter the filter in parentheses
         * @return The Builder with the filter in parentheses added.
         */
        public Builder or(Builder innerFilter) {
            filter.append(" or (").append(innerFilter.filter).append(")");
            return this;
        }

        /**
         * Adds the given {@link SortOrder} to the query
         *
         * @param sortOrder The order in which to sort the result
         * @return The Builder with this sort oder added.
         */
        public Builder withSortOrder(SortOrder sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        /**
         * Add the start Index from where on the list will be returned to the query
         *
         * @param startIndex The position to use as the first entry in the result.
         * @return The Builder with this start Index added.
         */
        public Builder startIndex(int startIndex) {
            this.startIndex = startIndex;
            return this;
        }

        /**
         * Add the number of wanted results per page to the query
         *
         * @param count The number of items displayed per page.
         * @return The Builder with this count per page added.
         */
        public Builder countPerPage(int count) {
            this.countPerPage = count;
            return this;
        }

        /**
         * Add the wanted attribute names to the sortBy statement.
         *
         * @param attributeName attributes to sort by the query
         * @return The Builder with sortBy added.
         */
        public Builder sortBy(String attributeName) {
            if (!(isAttributeValid(attributeName))) {
                throw new InvalidAttributeException("Sorting for this attribute is not supported");
            }
            sortBy = attributeName;
            return this;
        }

        /**
         * Build the query String to use against OSIAM.
         *
         * @return The query as a String
         */
        public Query build() throws UnsupportedEncodingException  {
            StringBuilder builder = new StringBuilder();
            if (filter.length() != 0) {
                ensureQueryParamIsSeparated(builder);
                builder.append("filter=")
                	.append(URLEncoder.encode(filter.toString(), Charsets.UTF_8.name()));
            }
            if (sortBy != null) {
                ensureQueryParamIsSeparated(builder);
                builder.append("sortBy=")
                        .append(sortBy);
            }
            if (sortOrder != null) {
                ensureQueryParamIsSeparated(builder);
                builder.append("sortOrder=")
                        .append(sortOrder);

            }
            if (countPerPage != DEFAULT_COUNT_PER_PAGE) {
                ensureQueryParamIsSeparated(builder);
                builder.append("count=")
                        .append(countPerPage);
            }
            if (startIndex != DEFAULT_START_INDEX) {
                ensureQueryParamIsSeparated(builder);
                builder.append("startIndex=")
                        .append(startIndex);
            }
            return new Query(builder.toString());
        }

        private void ensureQueryParamIsSeparated(StringBuilder builder) {
            if (builder.length() != 0) {
                builder.append("&");
            }
        }

        private Filter query(String attributeName) {
            if (!(isAttributeValid(attributeName))) {
                throw new InvalidAttributeException("Querying for this attribute is not supported");
            }

            filter.append(attributeName);
            return new Filter(this);
        }

        private boolean isAttributeValid(String attribute) {
            return isAttributeValid(attribute, clazz);
        }

        private boolean isAttributeValid(String attribute, Class clazz) {
            String compositeField = "";
            if (attribute.contains(".")) {
                compositeField = attribute.substring(attribute.indexOf('.') + 1);
            }
            if (attribute.startsWith("meta.")) {
                return isAttributeValid(compositeField, org.osiam.resources.scim.Meta.class);
            }
            if (attribute.startsWith("emails.")) {
                return isAttributeValid(compositeField, org.osiam.resources.scim.MultiValuedAttribute.class);
            }
            if (attribute.startsWith("name.")) {
                return isAttributeValid(compositeField, org.osiam.resources.scim.Name.class);
            }

            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isPrivate(field.getModifiers()) && field.getName().equalsIgnoreCase(attribute)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * A Filter is used to produce filter criteria for the query. At this point the conditions are mere strings.
     * This is going to change.
     */
    public static final class Filter {

        private Builder qb;

        private Filter(Builder builder) {
            this.qb = builder;
        }

        private Builder addFilter(String filter, String condition) {
            qb.filter.append(filter);

            if (condition != null && condition.length() > 0) {
                qb.filter.append("\"").
                        append(condition).
                        append("\"");
            }
            return qb;
        }

        /**
         * Add a condition the attribute filtered for is equal to.
         *
         * @param condition The condition to meet.
         * @return The Builder with this filter added.
         */
        public Builder equalTo(String condition) {
            return addFilter(" eq ", condition);
        }

        /**
         * Add a condition the attribute filtered on should contain.
         *
         * @param condition The condition to meet.
         * @return The Builder with this filter added.
         */
        public Builder contains(String condition) {
            return addFilter(" co ", condition);
        }

        /**
         * Add a condition the attribute filtered on should contain.
         *
         * @param condition The condition to meet.
         * @return The Builder with this filter added.
         */
        public Builder startsWith(String condition) {
            return addFilter(" sw ", condition);
        }

        /**
         * Make sure that the attribute for this filter is present.
         *
         * @return The Builder with this filter added.
         */
        public Builder present() {
            return addFilter(" pr ", "");
        }

        /**
         * Add a condition the attribute filtered on should be greater than.
         *
         * @param condition The condition to meet.
         * @return The Builder with this filter added.
         */
        public Builder greaterThan(String condition) {
            return addFilter(" gt ", condition);
        }

        /**
         * Add a condition the attribute filtered on should be greater than or equal to.
         *
         * @param condition The condition to meet.
         * @return The Builder with this filter added.
         */
        public Builder greaterEquals(String condition) {
            return addFilter(" ge ", condition);
        }

        /**
         * Add a condition the attribute filtered on should be less than.
         *
         * @param condition The condition to meet.
         * @return The Builder with this filter added.
         */
        public Builder lessThan(String condition) {
            return addFilter(" lt ", condition);
        }

        /**
         * Add a condition the attribute filtered on should be less than or equal to.
         *
         * @param condition The condition to meet.
         * @return The Builder with this filter added.
         */
        public Builder lessEquals(String condition) {
            return addFilter(" le ", condition);
        }
    }
}