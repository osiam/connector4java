package org.osiam.client.query.metamodel;

import java.util.Locale;

import org.osiam.resources.type.EmailType;
/*
* for licensing see the file license.txt.
*/

/**
 * a single Type attributes from a User
 */
public class EmailTypeAttribute extends Attribute{

    EmailTypeAttribute(){
        this.value = "emails.type";
    }

    /**
     * return a eq comparison of the Attribute and the given filter
     * @param filter the wanted filter
     * @return an eq comparison
     */
    public Comparison equalTo(EmailType filter){
        return new Comparison(value + " eq \"" + filter.name().toLowerCase(Locale.ENGLISH) + "\"");
    }
}
