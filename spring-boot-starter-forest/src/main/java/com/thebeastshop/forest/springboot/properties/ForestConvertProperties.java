package com.thebeastshop.forest.springboot.properties;

public class ForestConvertProperties {

    private ForestConverterItemProperties text;

    private ForestConverterItemProperties json;

    private ForestConverterItemProperties xml;

    private ForestConverterItemProperties binary;

    public ForestConverterItemProperties getText() {
        return text;
    }

    public void setText(ForestConverterItemProperties text) {
        this.text = text;
    }

    public ForestConverterItemProperties getJson() {
        return json;
    }

    public void setJson(ForestConverterItemProperties json) {
        this.json = json;
    }

    public ForestConverterItemProperties getXml() {
        return xml;
    }

    public void setXml(ForestConverterItemProperties xml) {
        this.xml = xml;
    }

    public ForestConverterItemProperties getBinary() {
        return binary;
    }

    public void setBinary(ForestConverterItemProperties binary) {
        this.binary = binary;
    }
}
