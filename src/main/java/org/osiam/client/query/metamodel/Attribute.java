package org.osiam.client.query.metamodel;
/*
 * for licensing see the file license.txt.
 */

/**
 * single Group or User Attribute
 */
public abstract class Attribute {

    protected String value = "";   // NOSONAR - false-positive from clover; visibility can't be private

    Attribute(){}

    public String toString(){
        return value;
    }
}
