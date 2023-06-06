package com.shadow.web.model.enums;

public enum DevicePropEnum {

    INT("int", "int"),
    FLOAT("float", "float"),
    DOUBLE("double", "double"),
    BOOLEAN("boolean", "boolean"),
    TEXT("text", "String"),
    DATE("date", "Date");

    private final String dbConstruction;
    private final String xmlConstruction;

    DevicePropEnum(String dbConstruction, String xmlConstruction) {
        this.dbConstruction = dbConstruction;
        this.xmlConstruction = xmlConstruction;
    }

    public String getDbConstruction() {
        return this.dbConstruction;
    }

    public String getXmlConstruction() {
        return this.xmlConstruction;
    }

    public static DevicePropEnum getEnum(String dbConstruction) {
        for (DevicePropEnum propEnum : DevicePropEnum.values()) {
            if (propEnum.getDbConstruction().equals(dbConstruction)) {
                return propEnum;
            }
        }

        return TEXT;
    }

}
