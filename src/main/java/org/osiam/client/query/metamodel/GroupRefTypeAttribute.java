package org.osiam.client.query.metamodel;

import java.util.Locale;

import org.osiam.resources.type.GroupRefType;
/*
* for licensing see the file license.txt.
*/

/**
 * a single Type attributes from a User
 */
public class GroupRefTypeAttribute extends Attribute{

    GroupRefTypeAttribute(){
        this.value = "groups.type";
    }

    /**
     * return a eq comparison of the Attribute and the given filter
     * @param filter the wanted filter
     * @return an eq comparison
     */
    public Comparison equalTo(GroupRefType filter){
        return new Comparison(value + " eq \"" + filter.name().toLowerCase(Locale.ENGLISH) + "\"");
    }
}
