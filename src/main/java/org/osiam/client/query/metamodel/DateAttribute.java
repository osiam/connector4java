package org.osiam.client.query.metamodel;
/*
* for licensing see the file license.txt.
*/

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * single Date Attribute from a Group or a User
 */
public class DateAttribute extends Attribute{

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    

    DateAttribute(String value){
        this.value = value;
    }

    /**
     * return a eq comparison of the Attribute and the given filter
     * @param filter the wanted filter
     * @return an eq comparison
     */
    public Comparison equalTo(DateTime filter){
        return new Comparison(value + " eq \"" + DATE_FORMAT.print(filter) + "\"");
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
    public Comparison greaterThan(DateTime filter) {
        return new Comparison(value + " gt \"" + DATE_FORMAT.print(filter)  + "\"");
    }

    /**
     * return a ge comparison of the Attribute and the given filter
     * @param filter the wanted filter
     * @return an ge comparison
     */
    public Comparison greaterEquals(DateTime filter) {
        return new Comparison(value + " ge \"" + DATE_FORMAT.print(filter)  + "\"");
    }

    /**
     * return a lt comparison of the Attribute and the given filter
     * @param filter the wanted filter
     * @return an lt comparison
     */
    public Comparison lessThan(DateTime filter) {
        return new Comparison(value + " lt \"" + DATE_FORMAT.print(filter)  + "\"");
    }

    /**
     * return a le comparison of the Attribute and the given filter
     * @param filter the wanted filter
     * @return an le comparison
     */
    public Comparison lessEquals(DateTime filter) {
        return new Comparison(value + " le \"" + DATE_FORMAT.print(filter) + "\"");
    }
}
