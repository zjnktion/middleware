package cn.zjnktion.middleware.ioframework;

/**
 * @author zjnktion
 */
public enum IdleType {

    READ_IDLE("read idle"),
    WRITE_IDLE("write idle"),
    BOTH_IDLE("both idle");

    private String typeName;

    IdleType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
