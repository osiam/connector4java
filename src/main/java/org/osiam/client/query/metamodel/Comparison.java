package org.osiam.client.query.metamodel;
/*
 * for licensing see the file license.txt.
 */

/**
 * a comparision between a Attribute and a value
 */
public class Comparison {

    private String filter;

    Comparison(String filter){
        this.filter = filter;
    }

    public String toString(){
        return filter;
    }
}
