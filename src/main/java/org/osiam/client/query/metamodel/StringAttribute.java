package org.osiam.client.query.metamodel;
/*
* for licensing see the file license.txt.
*/

/**
 * a single String attributes from a User or a Group
 */
public class StringAttribute extends Attribute{

    StringAttribute(String value){
        this.value = value;
    }

    /**
     * return a eq comparison of the Attribute and the given filter
     * @param filter the wanted filter
     * @return an eq comparison
     */
    public Comparison equalTo(String filter){
        return new Comparison(value + " eq \"" + filter + "\"");
    }

    /**
     * return a co comparison of the Attribute and the given filter
     * @param filter the wanted filter
     * @return an co comparison
     */
    public Comparison contains(String filter){
        return new Comparison(value + " co \"" + filter + "\"");
    }

    /**
     * return a sw comparison of the Attribute and the given filter
     * @param filter the wanted filter
     * @return an sw comparison
     */
    public Comparison startsWith(String filter) {
        return new Comparison(value + " sw \"" + filter + "\"");
    }

    /**
     * return a pr comparison of the Attribute
     * @return an pr comparison
     */
    public Comparison present() {
        return new Comparison(value + " pr ");
    }
}
