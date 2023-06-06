package com.shadow.web.model.enums;

public enum VersionEnum {

    A("A", 0),
    B("B", 1),
    C("C", 2);

    private final String code;
    private final int level;

    VersionEnum(String code, int level) {
        this.code = code;
        this.level = level;
    }

    public String getCode() {
        return code;
    }

    public static VersionEnum getEnumByCode(String code) {
        for (VersionEnum version : VersionEnum.values()) {
            if (version.code.equals(code)) {
                return version;
            }
        }

        return A;
    }

    public static VersionEnum getEnumByLevel(int level) {
        for (VersionEnum version : VersionEnum.values()) {
            if (version.level == level) {
                return version;
            }
        }

        return A;
    }

}
