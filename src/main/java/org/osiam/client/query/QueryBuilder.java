package org.osiam.client.query;
/*
 * for licensing see the file license.txt.
 */

import org.osiam.client.exception.InvalidAttributeException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * The QueryBuilder provides a fluent api to help building queries against the OSIAM Service.
 */
public class QueryBuilder {

    static final private int DEFAULT_START_INDEX = 0;
    static final private int DEFAULT_COUNT_PER_PAGE = 100;
    private Class clazz;
    private StringBuilder filter;
    private SortOrder sortOrder;
    private int startIndex = DEFAULT_START_INDEX;
    private int countPerPage = DEFAULT_COUNT_PER_PAGE;

    /**
     * The Constructor of the QueryBuilder
     *
     * @param clazz The class of Resources to query for.
     */
    public QueryBuilder(Class clazz) {
        filter = new StringBuilder();
        this.clazz = clazz;
    }

    /**
     * Add a filter on the given Attribute.
     *
     * @param attributeName The name of the attribute to filter on.
     * @return A {@link QueryBuilder.Filter} to specify the filtering criteria
     * @throws InvalidAttributeException if the given attribute is not valid for a query
     */
    public Filter filter(String attributeName) {
        return query(attributeName);
    }

    /**
     * Add an 'logical and' operation to the filter with another attribute to filter on.
     *
     * @param attributeName The name of the attribute to filter the and clause on.
     * @return A {@link QueryBuilder.Filter} to specify the filtering criteria
     * @throws InvalidAttributeException if the given attribute is not valid for a query
     */
    public Filter and(String attributeName) {
        filter.append(" and ");
        return query(attributeName);
    }

    /**
     * Adds the query of the given QueryBuilder into ( and ) to the filter
     * @param innerQuery the inner filter
     * @return The QueryBuilder with the inner filter added.
     */
    public QueryBuilder and(QueryBuilder innerQuery){
        builder.append(" and (").append(innerQuery.build().toString()).append(")");
        return this;
    }

    /**
     * Add an 'logical or' operation to the filter with another attribute to filter on.
     *
     * @param attributeName The name of the attribute to filter the or clause on.
     * @return A {@link QueryBuilder.Filter} to specify the filtering criteria
     * @throws InvalidAttributeException if the given attribute is not valid for a query
     */
    public Filter or(String attributeName) {
        filter.append(" or ");
        return query(attributeName);
    }

    private Filter query(String attributeName) {
        if (!(isAttributeValid(attributeName))) {
            throw new InvalidAttributeException("Querying for this attribute is not supported");
        }

        filter.append(attributeName);
        return new Filter(this);
    }

    /**
     * Adds the query of the given QueryBuilder into ( and ) to the filter
     * @param innerQuery the inner filter
     * @return The QueryBuilder with the inner filter added.
     */
    public QueryBuilder or(QueryBuilder innerQuery){
        builder.append(" or (").append(innerQuery.build().toString()).append(")");
        return this;
    }

    /**
     * Adds the given {@link SortOrder} to the query
     *
     * @param sortOrder The order in which to sort the result
     * @return The QueryBuilder with this sort oder added.
     */
    public QueryBuilder withSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    /**
     * Add the start Index from where on the list will be returned to the query
     *
     * @param startIndex The position to use as the first entry in the result.
     * @return The QueryBuilder with this start Index added.
     */
    public QueryBuilder startIndex(int startIndex) {
        this.startIndex = startIndex;
        return this;
    }

    /**
     * Add the number of wanted results per page to the query
     *
     * @param count The number of items displayed per page.
     * @return The QueryBuilder with this count per page added.
     */
    public QueryBuilder countPerPage(int count) {
        this.countPerPage = count;
        return this;
    }

    /**
     * Build the query String to use against OSIAM.
     *
     * @return The query as a String
     */
    public Query build() {
        StringBuilder builder = new StringBuilder();
        if (filter.length() != 0) {
            ensureQueryParamIsSeparated(builder);
            builder.append("filter=")
                    .append(filter);
        }

        if (sortOrder != null) {
            ensureQueryParamIsSeparated(builder);
            builder.append(sortOrder);

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

    private boolean isAttributeValid(String attribute) {
        return isAttributeValid(attribute, clazz);
    }

    /**
     * A Filter is used to produce filter criteria for the query. At this point the conditions are mere strings.
     * This is going to change.
     */
    public class Filter {

        private QueryBuilder qb;

        private Filter(QueryBuilder queryBuilder) {
            this.qb = queryBuilder;
        }

        private QueryBuilder addFilter(String filter, String condition) {
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
         * @return The QueryBuilder with this filter added.
         */
        public QueryBuilder equalTo(String condition) {
            return addFilter(" eq ", condition);
        }

        /**
         * Add a condition the attribute filtered on should contain.
         *
         * @param condition The condition to meet.
         * @return The QueryBuilder with this filter added.
         */
        public QueryBuilder contains(String condition) {
            return addFilter(" co ", condition);
        }

        /**
         * Add a condition the attribute filtered on should contain.
         *
         * @param condition The condition to meet.
         * @return The QueryBuilder with this filter added.
         */
        public QueryBuilder startsWith(String condition) {
            return addFilter(" sw ", condition);
        }

        /**
         * Make sure that the attribute for this filter is present.
         *
         * @return The QueryBuilder with this filter added.
         */
        public QueryBuilder present() {
            return addFilter(" pr ", "");
        }

        /**
         * Add a condition the attribute filtered on should be greater than.
         *
         * @param condition The condition to meet.
         * @return The QueryBuilder with this filter added.
         */
        public QueryBuilder greaterThan(String condition) {
            return addFilter(" gt ", condition);
        }

        /**
         * Add a condition the attribute filtered on should be greater than or equal to.
         *
         * @param condition The condition to meet.
         * @return The QueryBuilder with this filter added.
         */
        public QueryBuilder greaterEquals(String condition) {
            return addFilter(" ge ", condition);
        }

        /**
         * Add a condition the attribute filtered on should be less than.
         *
         * @param condition The condition to meet.
         * @return The QueryBuilder with this filter added.
         */
        public QueryBuilder lessThan(String condition) {
            return addFilter(" lt ", condition);
        }

        /**
         * Add a condition the attribute filtered on should be less than or equal to.
         *
         * @param condition The condition to meet.
         * @return The QueryBuilder with this filter added.
         */
        public QueryBuilder lessEquals(String condition) {
            return addFilter(" le ", condition);
        }
    }
}
