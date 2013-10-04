package org.osiam.client.query.metamodel;

/*
* for licensing see the file license.txt.
*/
import org.osiam.resources.type.MemberType;

/**
 * a single Type attributes from a Group
 */
public class MemberTypeAttribute extends Attribute{

    MemberTypeAttribute(){
        this.value = "members.type";
    }

    /**
     * return a eq comparison of the Attribute and the given filter
     * @param filter the wanted filter
     * @return an eq comparison
     */
    public Comparison equalTo(MemberType filter){
        return new Comparison(value + " eq \"" + filter.name().toLowerCase() + "\"");
    }
}
