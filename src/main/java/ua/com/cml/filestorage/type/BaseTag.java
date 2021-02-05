package ua.com.cml.filestorage.type;

public enum BaseTag {

    AUDIO,
    VIDEO,
    DOCUMENT,
    IMAGE;

    public String getName() {
        return this.name().toLowerCase();
    }
}
