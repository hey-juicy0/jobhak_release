package univcapstone.employmentsite.domain;



public enum Authority {
    USER("ROLE_USER"),
    ADMIN("ROLE_USER");

    private String role;

    Authority(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
