package org.osiam.client.query.metamodel;
/*
 * for licensing see the file license.txt.
 */

/**
 * single Group or User Attribute
 */
public abstract class Attribute {

    protected String value = "";

    Attribute(){}

    public String toString(){
        return value;
    }
}
