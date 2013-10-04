package org.osiam.client.query.metamodel;

import org.osiam.resources.type.PhotoType;
/*
* for licensing see the file license.txt.
*/

/**
 * a single Type attributes from a User
 */
public class PhotoTypeAttribute extends Attribute{

    PhotoTypeAttribute(){
        this.value = "photos.type";
    }

    /**
     * return a eq comparison of the Attribute and the given filter
     * @param filter the wanted filter
     * @return an eq comparison
     */
    public Comparison equalTo(PhotoType filter){
        return new Comparison(value + " eq \"" + filter.name().toLowerCase() + "\"");
    }
}
