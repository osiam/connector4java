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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.Charsets;
import org.osiam.client.exception.InvalidAttributeException;
import org.osiam.client.query.metamodel.Attribute;
import org.osiam.client.query.metamodel.Comparison;
import org.osiam.resources.scim.Resource;

/**
 * This class represents a query as it is run against the OSIAM service.
 * @deprecated See {@link org.osiam.client.nquery.Query}
 */
@Deprecated
public class Query {
    private static final int DEFAULT_COUNT = 100;
    private static final int DEFAULT_INDEX = 1;
    private static final Pattern INDEX_PATTERN = Pattern.compile("startIndex=(\\d+)&?");
    private static final Pattern COUNT_PATTERN = Pattern.compile("count=(\\d+)&?");

    private final String queryString;

    private Matcher indexMatcher;
    private Matcher countMatcher;

    /**
     * @deprecated See {@link org.osiam.client.nquery.QueryBuilder}
     */
    @Deprecated
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
    public int getCountPerPage() {
        if (queryStringContainsCount()) {
            return Integer.parseInt(countMatcher.group(1));
        }
        return DEFAULT_COUNT;
    }

    /**
     * Returns the startIndex of this query. If no startIndex was set, it returns the default, which is 1.
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
        String nextIndex = "startIndex=" + (getCountPerPage() + getStartIndex());
        if (queryStringContainsIndex()) {
            return new Query(indexMatcher.replaceFirst(nextIndex));
        }
        return new Query(queryString + "&" + nextIndex);
    }

    /**
     * Create a new query that is moved backward the result set by one page.
     *
     * @return A new query paged backward by one.
     * @throws IllegalStateException in case you are already at the first page
     */
    public Query previousPage() {
        if(getStartIndex() <= DEFAULT_INDEX){
        	throw new IllegalStateException("StartIndex < " + DEFAULT_INDEX + " is not possible.");
        }
    	int newIndex = getStartIndex() - getCountPerPage();

        if (newIndex < DEFAULT_INDEX) {
            newIndex = DEFAULT_INDEX;
        }

        return new Query(indexMatcher.replaceFirst("startIndex=" + newIndex));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
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
    @Override
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

        private Class<? extends Resource> clazz;
        private String filter;
        private String sortBy;
        private SortOrder sortOrder;
        private int startIndex = DEFAULT_INDEX;
        private int countPerPage = DEFAULT_COUNT;

        /**
         * The Constructor of the QueryBuilder
         *
         * @param clazz The class of Resources to query for.
         */
        public Builder(Class<? extends Resource> clazz) {
            this.clazz = clazz;
        }

        /**
         * Add a filter on the given Attribute.
         *
         * @param filter The filter of the attributes to filter on.
         * @return The Builder with this filter added.
         */
        public Builder setFilter(Filter filter) {
            this.filter = filter.toString();
            return this;
        }

        /**
         * Add a filter on the given Attribute.
         *
         * @param filter The filter of the attributes to filter on.
         * @return The Builder with this filter added.
         */
        public Builder setFilter(String filter) {
            this.filter = filter;
            return this;
        }

        /**
         * Adds the given {@link SortOrder} to the query
         *
         * @param sortOrder The order in which to sort the result
         * @return The Builder with this sort order added.
         */
        public Builder setSortOrder(SortOrder sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        /**
         * Add the start Index from where on the list will be returned to the query
         *
         * @param startIndex The position to use as the first entry in the result.
         * @return The Builder with this start Index added.
         */
        public Builder setStartIndex(int startIndex) {
            this.startIndex = startIndex;
            return this;
        }

        /**
         * Add the number of wanted results per page to the query
         *
         * @param count The number of items displayed per page.
         * @return The Builder with this count per page added.
         */
        public Builder setCountPerPage(int count) {
            countPerPage = count;
            return this;
        }

        /**
         * Add the wanted attribute names to the sortBy statement.
         *
         * @param attribute attributes to sort by the query
         * @return The Builder with sortBy added.
         */
        public Builder setSortBy(Attribute attribute) {
            if (!isAttributeValid(attribute.toString())) {
                throw new InvalidAttributeException("Sorting for this attribute is not supported");//TODO
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
            if (filter != null) {
                try{
                ensureQueryParamIsSeparated(builder);
                builder.append("filter=")
                	.append(URLEncoder.encode(filter, Charsets.UTF_8.name()));
                }catch(UnsupportedEncodingException e)    {
                    throw new RuntimeException(e);  // NOSONAR - The UnsupportedEncodingException will in real time "never" happen and if yes a runtime exception will catch the problem
                }
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
            if (countPerPage != DEFAULT_COUNT) {
                ensureQueryParamIsSeparated(builder);
                builder.append("count=")
                        .append(countPerPage);
            }
            if (startIndex != DEFAULT_INDEX) {
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

        private boolean isAttributeValid(String attribute) {
            return isAttributeValid(attribute, clazz);
        }

        private static boolean isAttributeValid(String attribute, Class<?> clazz) {
            String compositeField = "";
            if (attribute.contains(".")) {
                compositeField = attribute.substring(attribute.indexOf('.') + 1);
            }
            if (attribute.startsWith("meta.")) {
                return isAttributeValid(compositeField, org.osiam.resources.scim.Meta.class);
            }
            if (attribute.startsWith("emails.")) {
                return isAttributeValid(compositeField, org.osiam.resources.scim.Email.class);
            }
            if (attribute.startsWith("name.")) {
                return isAttributeValid(compositeField, org.osiam.resources.scim.Name.class);
            }
            if (attribute.startsWith("groups.") || attribute.startsWith("members.")) {
                return isAttributeValid(compositeField, org.osiam.resources.scim.MemberRef.class);
            }

            List<String> fields = getAllClassFields(clazz);
            if(fields.contains(attribute)){
            	return true;
            }
            return false;
        }
    }

    private static List<String> getAllClassFields(Class<?> clazz){
    	ArrayList<String> fields = new ArrayList<>();
        for (Field actField : clazz.getDeclaredFields()) {
            fields.add(actField.getName());
        }
    	addFieldsFromSuperClass(fields, clazz.getSuperclass());
    	return fields;
    }

    private static void addFieldsFromSuperClass(List<String> fields, Class<?> clazz){
        if(clazz == null){
        	return;
        }
    	for (Field actField : clazz.getDeclaredFields()) {
            fields.add(actField.getName());
        }
    	addFieldsFromSuperClass(fields, clazz.getSuperclass());
    }

    /**
     * A Filter is used to produce filter criteria for the query.
     */
    public static final class Filter {

        private Class<?> clazz;
        private StringBuilder filterBuilder;

        //TODO this constructor is only valid for the not operator. Will be changed when revising the connector api.
        /**
         * The Constructor for the not operator of the Filter, will create invalid queries for other operators.
         *
         * @param clazz The class of Resources to filter for.
         */
        public Filter(Class<?> clazz) {
            filterBuilder = new StringBuilder();
            this.clazz = clazz;
        }

        /**
         * The Constructor of the Filter
         *
         * @param clazz The class of Resources to filter for.
         * @param filter First inner Filter that has to be added to a new Filter
         */
        public Filter(Class<?> clazz, Filter filter) {
            filterBuilder = new StringBuilder();
            this.clazz = clazz;
            filterBuilder.append(" (").append(filter.toString()).append(")");
        }

        /**
         * The Constructor of the Filter
         *
         * @param clazz The class of Resources to filter for.
         * @param comparison First Comparison that has to be added to a new Filter
         */
        public Filter(Class<?> clazz, Comparison comparison) {
            filterBuilder = new StringBuilder();
            this.clazz = clazz;
            query(comparison);
        }

        /**
         * Add an 'logical and' operation to the comparison with another attribute to comparison on.
         *
         * @param comparison The name of the attribute to comparison the and clause on.
         * @return A {@link org.osiam.client.query.metamodel.Comparison} to specify the filtering criteria
         * @throws org.osiam.client.exception.InvalidAttributeException if the given attribute is not valid for a query
         */
        public Filter and(Comparison comparison) {
            filterBuilder.append(" and ");
            return query(comparison);
        }

        /**
         * Adds the query of the given Builder with an 'logical and' into an ( ... ) to the filter
         *
         * @param innerFilter the inner filter
         * @return The Builder with the inner filter added.
         */
        public Filter and(Filter innerFilter) {
            if (innerFilter.toString().startsWith("not (")) {
                filterBuilder.append(" and ").append(innerFilter.toString());
            } else {
                filterBuilder.append(" and (").append(innerFilter.toString()).append(")");
            }
            return this;
        }

        /**
         * Add an 'logical or' operation to the comparison with another attribute to comparison on.
         *
         * @param comparison The name of the attribute to comparison the and clause on.
         * @return A {@link org.osiam.client.query.metamodel.Comparison} to specify the filtering criteria
         * @throws org.osiam.client.exception.InvalidAttributeException if the given attribute is not valid for a query
         */
        public Filter or(Comparison comparison) {
            filterBuilder.append(" or ");
            return query(comparison);
        }

        /**
         * Adds the query of the given Builder an 'logical or' into ( ... ) to the filter
         *
         * @param innerFilter the inner filter
         * @return The Builder with the inner filter added.
         */
        public Filter or(Filter innerFilter) {
            if (innerFilter.toString().startsWith("not (")) {
                filterBuilder.append(" or ").append(innerFilter.toString());
            } else {
                filterBuilder.append(" or (").append(innerFilter.toString()).append(")");
            }
            return this;
        }

        /**
         * Appends the not operator to the filter and adds the given parameter into ( ... )
         * @param comparison A filter string with valid attribute, operator and value to search for
         * @return The Builder with the filter added.
         * @throws org.osiam.client.exception.InvalidAttributeException if the given attribute is not valid for a query
         */
        public Filter not(Comparison comparison) {
            filterBuilder.append("not (");
            query(comparison);
            filterBuilder.append(")");
            return this;
        }

        /**
         * Appends the not operator to the filter and adds the query of the given Builder into ( ... )
         * @param innerFilter the inner filter
         * @return The Builder with the inner filter added.
         */
        public Filter not(Filter innerFilter) {
            filterBuilder.append("not (").append(innerFilter.toString()).append(")");
            return this;
        }

        private boolean isAttributeValid(Comparison comparison) {
            String attribute = comparison.toString().substring(0, comparison.toString().indexOf(" "));
            return Builder.isAttributeValid(attribute, clazz);
        }

        /**
         * provides all appended Comparisons as String
         * @return the build together filter
         */
        @Override
        public String toString(){
            return filterBuilder.toString();
        }

        private Filter query(Comparison comparison) {
            if (!isAttributeValid(comparison)) {
                throw new InvalidAttributeException("Querying for this attribute is not supported");
            }

            filterBuilder.append(comparison.toString());
            return this;
        }
    }
}