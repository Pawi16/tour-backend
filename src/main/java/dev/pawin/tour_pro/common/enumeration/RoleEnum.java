package dev.pawin.tour_pro.common.enumeration;

public enum RoleEnum {
    CONSUMER(1),
    ADMIN(2),
    COMPANY(3);

    private final int id;

    RoleEnum(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }


    
}
