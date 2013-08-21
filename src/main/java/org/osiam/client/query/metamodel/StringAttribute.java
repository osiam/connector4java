package org.osiam.client.query.metamodel;

public class StringAttribute extends Attribute{

    StringAttribute(String value){
        this.value = value;
    }
    public Comparision equalTo(String filter){
        return new Comparision(value + " eq \"" + filter + "\"");
    }

    public Comparision contains(String filter){
        return new Comparision(value + " co \"" + filter + "\"");
    }

    public Comparision startsWith(String filter) {
        return new Comparision(value + " sw \"" + filter + "\"");
    }

    public Comparision present() {
        return new Comparision(value + " pr ");
    }
}
