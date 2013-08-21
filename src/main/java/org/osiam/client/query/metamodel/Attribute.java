package org.osiam.client.query.metamodel;

/**
 * Created with IntelliJ IDEA.
 * User: dmoeb
 * Date: 15.08.13
 * Time: 16:20
 * To change this template use File | Settings | File Templates.
 */
public abstract class Attribute {

    protected String value = "";

    Attribute(){}

    public String toString(){
        return value;
    }
}
