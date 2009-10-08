package se.vgregion.kivtools.hriv.intsvc.utils;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="item")

public class MarshallerClass{
    private int id = 0;
   
    @XmlAttribute(name="id", required=true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

 
}
