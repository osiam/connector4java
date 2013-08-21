package org.osiam.client.query.metamodel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: dmoeb
 * Date: 16.08.13
 * Time: 09:37
 * To change this template use File | Settings | File Templates.
 */
public class DateAttribute extends Attribute{

    private static DateFormat df;

    static {
        df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    }

    DateAttribute(String value){
        this.value = value;
    }
    public Comparision equalTo(Date filter){
        return new Comparision(value + " eq \"" + df.format(filter) + "\"");
    }

    public Comparision present() {
        return new Comparision(value + " pr ");
    }

    public Comparision greaterThan(Date filter) {
        return new Comparision(value + " gt \"" + df.format(filter) + "\"");
    }

    public Comparision greaterEquals(Date filter) {
        return new Comparision(value + " ge \"" + df.format(filter) + "\"");
    }

    public Comparision lessThan(Date filter) {
        return new Comparision(value + " lt \"" + df.format(filter) + "\"");
    }

    public Comparision lessEquals(Date filter) {
        return new Comparision(value + " le \"" + df.format(filter) + "\"");
    }
}
