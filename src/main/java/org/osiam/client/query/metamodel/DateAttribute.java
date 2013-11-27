/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.client.query.metamodel;

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
