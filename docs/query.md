If you want to search for Users or Groups you need to create an Query object

The QueryBuilder will help you to build the needed query to get any search
result you need. The Query object also gives you the possibility to move to the
next or the prevision page.


Chapters:
- [Creating a Query Object](#creating-a-query-object)
- [QueryBuilder](#querybuilder)
  - [creating the filter (where) statement](#creating-the-filter-where-statement)
  - [sorting](#sorting)
  - [results per page](#results-per-page)
  - [start index](#start-index)

# Creating a Query Object
A Query Object can be created by

```h
Query query = new QueryBuilder().....build();
```


If you have more Users or Groups than one page will hold (default 100) you can
get with

```java
Query newQuery = query.nextPage();
```

a new query which will gives you the results of the next page.

In the same way you can get the previous page with

```java
Query newQuery = query.previousPage();
```

If you are already at the "first" page you will get the same Query object.

# QueryBuilder

The QueryBuilder gives you a easy possibility to build any easy or complex
Queries you need.


## creating the filter (where) statement

You can set a String based filter by calling the method 

    queryBuilder.filter(<filter>);

Some examples are:

```java
String simpleFilter = "userName eq \"marissa\"";
String filterWithAnd = "addresses.country eq \"Germany\" and active eq \"true\"";
String filterWithNot = "addresses.country eq \"Germany\" and not(active eq \"true\")";
String filterForMultiValuedAttribute = "emails sw \"marissa\"";
String filterForMultiValuedAttributeValue = "emails.value sw \"marissa\"";
String filterForGroupMembership = "groups.display eq \"group1\"";
String complexFilter = "active eq \"true\" and(nickname eq \"hello\" or nickname eq \"world\")"
                + "and not(groups.display eq \"group1\" or groups.display eq \"group2\") "
                + "and meta.created gt \"" + QueryBuilder.getScimConformFormattedDateTime(dateTime) + "\"";
```

The following filter options are supported:
* eq = equals
* co = contains
* sw = starts with
* pr = present
* gt = greater than
* ge = greater equals
* lt = less than
* le = less equals

Not all filter options are supported with all fields. For example emails.type
can't be combined with le since it makes no sense.

- all filter parameters have to be surrounded by ". Also boolean parameters or
numbers

## sorting

OSIAM gives you the opportunity to sort your result by one field. To sort by
one field call

```java
queryBuilder.ascending(<sort Field>);
// or
queryBuilder.descending(<sort Field>);
```

## results per page

with
```java
queryBuilder.count(20);
```

you can define how many Users/Groups you want to get for the actual request.

## start index
with
 
```java
queryBuilder.startIndex(10);
```

you can define the 1-based index of the first search result.
