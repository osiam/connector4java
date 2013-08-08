package org.osiam.client.query;

import org.osiam.client.exception.QueryBuilderInitializationException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class QueryBuilder {


    Class type;
    StringBuilder builder;

    public QueryBuilder() {
        builder = new StringBuilder();
    }

    public QueryBuilder forClass(Class clazz) {
        this.type = clazz;
        return this;
    }

    public Filter query(final String attributeName) {
        if (!isTypeSet()) {
            throw new QueryBuilderInitializationException("The Type for the query has not been set.");
        }
        if (!(isAttributeValid(attributeName))) {
            throw new QueryBuilderInitializationException("Querying for this attribute is not supported");
        }

        builder.append(attributeName.toLowerCase());
        return new Filter(this);
    }

    public Filter and(String attributeName) {
        builder.append(" and ");
        return query(attributeName);
    }

    public Filter or(String attributeName) {
        builder.append(" or ");
        return query(attributeName);
    }

    public String build() {
        return builder.toString();
    }

    private boolean isTypeSet() {
        return type != null;
    }

    private boolean isAttributeValid(String attribute, Class clazz) {
        attribute = attribute.toLowerCase();
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

    public boolean isAttributeValid(String attribute) {
        return isAttributeValid(attribute, type);
    }


    public class Filter {

        private QueryBuilder qb;

        private Filter(QueryBuilder queryBuilder) {
            this.qb = queryBuilder;
        }

        private QueryBuilder addFilter(String filter, String condition) {
            qb.builder.append(filter).
                    append(condition);
            return qb;
        }

        public QueryBuilder equals(String condition) {
            return addFilter(" eq ", condition);
        }

        public QueryBuilder contains(String condition) {
            return addFilter(" co ", condition);
        }

        public QueryBuilder startsWith(String condition) {
            return addFilter(" sw ", condition);
        }

        public QueryBuilder present(String condition) {
            return addFilter(" pr ", condition);
        }

        public QueryBuilder greaterThan(String condition) {
            return addFilter(" gt ", condition);
        }

        public QueryBuilder greaterEquals(String condition) {
            return addFilter(" ge ", condition);
        }

        public QueryBuilder lessThan(String condition) {
            return addFilter(" lt ", condition);
        }

        public QueryBuilder lessEquals(String condition) {
            return addFilter(" le ", condition);
        }
    }
}
