package com.daisyworks.demo;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A generic widget object that can be serialized; modify to suit your needs.
 */
@XmlRootElement(name = "port")
public class Port {

    private String name;

    public Port() {
    }

    public Port(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("{name:%s}", name);
    }
}
