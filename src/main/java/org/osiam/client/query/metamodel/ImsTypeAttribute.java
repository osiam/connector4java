package org.osiam.client.query.metamodel;

import org.osiam.resources.type.ImsType;
/*
* for licensing see the file license.txt.
*/

/**
 * a single Type attributes from a User
 */
public class ImsTypeAttribute extends Attribute{

    ImsTypeAttribute(){
        this.value = "ims.type";
    }

    /**
     * return a eq comparison of the Attribute and the given filter
     * @param filter the wanted filter
     * @return an eq comparison
     */
    public Comparison equalTo(ImsType filter){
        return new Comparison(value + " eq \"" + filter.name().toLowerCase() + "\"");
    }
}
