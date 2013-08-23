package org.osiam.client.query.metamodel;
/*
* for licensing see the file license.txt.
*/

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * single Date Attribute from a Group or a User
 */
public class DateAttribute extends Attribute{

    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    DateAttribute(String value){
        this.value = value;
    }

    /**
     * return a eq comparison of the Attribute and the given filter
     * @param filter the wanted filter
     * @return an eq comparison
     */
    public Comparison equalTo(Date filter){
        return new Comparison(value + " eq \"" + df.format(filter) + "\"");
    }

    /**
     * return a pr comparison of the Attribute
     * @return an pr comparison
     */
    public Comparison present() {
        return new Comparison(value + " pr ");
    }

    /**
     * return a gt comparison of the Attribute and the given filter
     * @param filter the wanted filter
     * @return an gt comparison
     */
    public Comparison greaterThan(Date filter) {
        return new Comparison(value + " gt \"" + df.format(filter) + "\"");
    }

    /**
     * return a ge comparison of the Attribute and the given filter
     * @param filter the wanted filter
     * @return an ge comparison
     */
    public Comparison greaterEquals(Date filter) {
        return new Comparison(value + " ge \"" + df.format(filter) + "\"");
    }

    /**
     * return a lt comparison of the Attribute and the given filter
     * @param filter the wanted filter
     * @return an lt comparison
     */
    public Comparison lessThan(Date filter) {
        return new Comparison(value + " lt \"" + df.format(filter) + "\"");
    }

    /**
     * return a le comparison of the Attribute and the given filter
     * @param filter the wanted filter
     * @return an le comparison
     */
    public Comparison lessEquals(Date filter) {
        return new Comparison(value + " le \"" + df.format(filter) + "\"");
    }
}
