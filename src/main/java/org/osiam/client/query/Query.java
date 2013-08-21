package org.osiam.client.query;
/*
 * for licensing see the file license.txt.
 */


import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.Charsets;
import org.osiam.client.exception.InvalidAttributeException;
import org.osiam.client.query.metamodel.Attribute;
import org.osiam.client.query.metamodel.Comparision;
import org.osiam.resources.scim.CoreResource;

/**
 * This class represents a query as it is run against the OSIAM service.
 */
public class Query {
    private static final int DEFAULT_COUNT = 100;
    // FIXME DEFAULT_INDEX should be 1 to comply to the Scim spec, but OSIAM server still depends on 0
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
        if (queryStringContainsCount()) { // NOSONAR - stmt in if is correct
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
        if (queryStringContainsIndex()) { // NOSONAR - stmt in if is correct
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
        if (queryStringContainsIndex()) { // NOSONAR - stmt in if is correct
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
        
        if (newIndex < DEFAULT_INDEX) { // NOSONAR - stmt in if is correct
            throw new IllegalStateException("Negative startIndex is not possible.");
        }
        
        return new Query(indexMatcher.replaceFirst("startIndex=" + newIndex));
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) { // NOSONAR - stmt in if is correct
            return true;
        }
        
        if (other == null || getClass() != other.getClass()) { // NOSONAR - stmt in if is correct
            return false;
        }

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

	// FIXME DEFAULT_START_INDEX should be 1 to comply to the Scim spec, but OSIAM server still depends on 0
        private static final int DEFAULT_START_INDEX = 0;
        private static final int DEFAULT_COUNT_PER_PAGE = 100;
        private Class<? extends CoreResource> clazz;
        private String filter;
        private String sortBy;
        private SortOrder sortOrder;
        private int startIndex = DEFAULT_START_INDEX;
        private int countPerPage = DEFAULT_COUNT_PER_PAGE;

        /**
         * The Constructor of the QueryBuilder
         *
         * @param clazz The class of Resources to query for.
         */
        public Builder(Class<? extends CoreResource> clazz) {
            this.clazz = clazz;
        }

        /**
         * Add a filter on the given Attribute.
         *
         * @param filter The name of the attribute to filter on.
         * @return A {@link Filter} to specify the filtering criteria
         * @throws org.osiam.client.exception.InvalidAttributeException if the given attribute is not valid for a query
         */
        public Builder filter(Filter filter) {
            this.filter = filter.toString();
            return this;
        }

        public Builder filter(String filter) {
            this.filter = filter;
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
         * @param attribute attributes to sort by the query
         * @return The Builder with sortBy added.
         */
        public Builder sortBy(Attribute attribute) {
            if (!(isAttributeValid(attribute.toString()))) {// NOSONAR - stmt in if is correct
                throw new InvalidAttributeException("Sorting for this attribute is not supported");
            }
            sortBy = attribute.toString();
            return this;
        }

        /**
         * Build the query String to use against OSIAM.
         *
         * @return The query as a String
         */
        public Query build()  {
            StringBuilder builder = new StringBuilder();
            if (filter != null) {// NOSONAR - stmt in if is correct
                try{
                ensureQueryParamIsSeparated(builder);
                builder.append("filter=")
                	.append(URLEncoder.encode(filter, Charsets.UTF_8.name()));
                }catch(UnsupportedEncodingException e)    {
                    throw new RuntimeException(e);
                }
            }
            if (sortBy != null) { // NOSONAR - stmt in if is correct
                ensureQueryParamIsSeparated(builder);
                builder.append("sortBy=")
                        .append(sortBy);
            }
            if (sortOrder != null) { // NOSONAR - stmt in if is correct
                ensureQueryParamIsSeparated(builder);
                builder.append("sortOrder=")
                        .append(sortOrder);

            }
            if (countPerPage != DEFAULT_COUNT_PER_PAGE) { // NOSONAR - stmt in if is correct
                ensureQueryParamIsSeparated(builder);
                builder.append("count=")
                        .append(countPerPage);
            }
            if (startIndex != DEFAULT_START_INDEX) { // NOSONAR - stmt in if is correct
                ensureQueryParamIsSeparated(builder);
                builder.append("startIndex=")
                        .append(startIndex);
            }
            return new Query(builder.toString());
        }

        private void ensureQueryParamIsSeparated(StringBuilder builder) {
            if (builder.length() != 0) { // NOSONAR - stmt in if is correct
                builder.append("&");
            }
        }

        private boolean isAttributeValid(String attribute) {
            return isAttributeValid(attribute, clazz);
        }

        private static boolean isAttributeValid(String attribute, Class clazz) {
            String compositeField = "";
            if (attribute.contains(".")) { // NOSONAR - stmt in if is correct
                compositeField = attribute.substring(attribute.indexOf('.') + 1);
            }
            if (attribute.startsWith("meta.")) { // NOSONAR - stmt in if is correct
                return isAttributeValid(compositeField, org.osiam.resources.scim.Meta.class);
            }
            if (attribute.startsWith("emails.")) { // NOSONAR - stmt in if is correct
                return isAttributeValid(compositeField, org.osiam.resources.scim.MultiValuedAttribute.class);
            }
            if (attribute.startsWith("name.")) { // NOSONAR - stmt in if is correct
                return isAttributeValid(compositeField, org.osiam.resources.scim.Name.class);
            }

            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isPrivate(field.getModifiers()) && field.getName().equalsIgnoreCase(attribute)) { // NOSONAR - stmt in if is correct
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

        private Class clazz;
        private StringBuilder filterBuilder;

        /**
         * The Constructor of the FilterBuilder
         *
         * @param clazz The class of Resources to query for.
         */
        public Filter(Class clazz) {
            filterBuilder = new StringBuilder();
            this.clazz = clazz;


        }public Filter startsWith(Comparision comparision) {
            return query(comparision);
        }

        /**
         * Add an 'logical and' operation to the comparision with another attribute to comparision on.
         *
         * @param comparision The name of the attribute to comparision the and clause on.
         * @return A {@link org.osiam.client.query.metamodel.Comparision} to specify the filtering criteria
         * @throws org.osiam.client.exception.InvalidAttributeException if the given attribute is not valid for a query
         */
        public Filter and(Comparision comparision) {
            filterBuilder.append(" and ");
            return query(comparision);
        }

        /**
         * Adds the query of the given Builder into ( and ) to the filter
         *
         * @param innerFilter the inner filter
         * @return The Builder with the inner filter added.
         */
        public Filter and(Filter innerFilter) {
            filterBuilder.append(" and (").append(innerFilter.toString()).append(")");
            return this;
        }

        /**
         * Add an 'logical or' operation to the comparision with another attribute to comparision on.
         *
         * @param comparision The name of the attribute to comparision the and clause on.
         * @return A {@link org.osiam.client.query.metamodel.Comparision} to specify the filtering criteria
         * @throws org.osiam.client.exception.InvalidAttributeException if the given attribute is not valid for a query
         */
        public Filter or(Comparision comparision) {
            filterBuilder.append(" or ");
            return query(comparision);
        }

        /**
         * Adds the query of the given Builder into ( or ) to the filter
         *
         * @param innerFilter the inner filter
         * @return The Builder with the inner filter added.
         */
        public Filter or(Filter innerFilter) {
            filterBuilder.append(" or (").append(innerFilter.toString()).append(")");
            return this;
        }

        private boolean isAttributeValid(Comparision comparision) {
            String attribute = comparision.toString().substring(0, comparision.toString().indexOf(" "));
            return Builder.isAttributeValid(attribute, clazz);
        }

        public String toString(){
            return filterBuilder.toString();
        }

        private Filter query(Comparision comparision) {
            if (!(isAttributeValid(comparision))) {
                throw new InvalidAttributeException("Querying for this attribute is not supported");
            }

            filterBuilder.append(comparision.toString());
            return this;
        }
    }
}