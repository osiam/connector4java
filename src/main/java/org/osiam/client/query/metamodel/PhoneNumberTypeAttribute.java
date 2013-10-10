package org.osiam.client.query.metamodel;

import java.util.Locale;

/*
* for licensing see the file license.txt.
*/
import org.osiam.resources.type.PhoneNumberType;

/**
 * a single Type attributes from a User
 */
public class PhoneNumberTypeAttribute extends Attribute{

    PhoneNumberTypeAttribute(){
        this.value = "phoneNumbers.type";
    }

    /**
     * return a eq comparison of the Attribute and the given filter
     * @param filter the wanted filter
     * @return an eq comparison
     */
    public Comparison equalTo(PhoneNumberType filter){
        return new Comparison(value + " eq \"" + filter.name().toLowerCase(Locale.ENGLISH) + "\"");
    }
}
