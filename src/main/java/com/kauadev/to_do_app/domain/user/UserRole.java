package com.kauadev.to_do_app.domain.user;

// como se fosse uma classe, porém, com valores estáticos.
public enum UserRole {
    ADMIN("admin"),
    USER("user");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
