package org.als.random.enums;

public enum TCEVersionEnum {
    TCE_6_3_5("6.3.5"),
    TCE_6_3_6("6.3.6"),
    TCE_6_3_7("6.3.7"),
    TCE_7_0_0("7.0.0"),
    TCE_7_1_0("7.1.0");

    private final String version;

    TCEVersionEnum(String version) {
        this.version = version;
    }

    public static TCEVersionEnum getTCEVersion(String version) {
        for( TCEVersionEnum tceVersion : TCEVersionEnum.values() ) {
            if( tceVersion.version.equals(version) ){
                return tceVersion;
            }
        }
        return null;
    }

    public String toString() {
        return this.version;
    }
}
