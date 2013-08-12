package org.osiam.client.query;

/**
 * This enum represents the way in which the result of a query should be ordered.
 */
public enum SortOrder {
    /**
     * Order result ascending
     */
    ASCENDING("sortOrder=ascending"),
    /**
     * Order result descending
     */
    DESCENDING("sortOrder=descending");

    private String sortOrder;

    SortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String toString() {
        return sortOrder;
    }
}