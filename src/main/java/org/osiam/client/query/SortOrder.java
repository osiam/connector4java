package org.osiam.client.query;

public enum SortOrder {
    ASCENDING("sortOrder=ascending"),
    DESCENDING("sortOrder=descending");

    private String sortOrder;

    SortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String toString() {
        return sortOrder;
    }
}
