package org.osiam.client.query.metamodel;

/**
 * Created with IntelliJ IDEA.
 * User: dmoeb
 * Date: 16.08.13
 * Time: 09:10
 * To change this template use File | Settings | File Templates.
 */
public class Comparision {

    private String filter;

    Comparision(String filter){
        this.filter = filter;
    }

    public String toString(){
        return filter;
    }
}
