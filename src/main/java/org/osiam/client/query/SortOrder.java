package org.osiam.client.query;
/*
* for licensing see the file license.txt.
*/

/**
 * This enum represents the way in which the result of a query should be ordered.
 */
public enum SortOrder {
    /**
     * Order result ascending
     */
    ASCENDING("ascending"),
    /**
     * Order result descending
     */
    DESCENDING("descending");

    private String sortOrder;

    SortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String toString() {
        return sortOrder;
    }
}