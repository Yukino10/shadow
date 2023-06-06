package com.cislc.shadow.utils.enums;

public enum Encryption {

    AES("aes"),
    RSA("rsa"),
    NONE("none");

    private final String name;

    Encryption(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Encryption getEncryption(String name) {
        for (Encryption encryption : Encryption.values()) {
            if (encryption.name.equals(name)) {
                return encryption;
            }
        }
        return NONE;
    }

}
