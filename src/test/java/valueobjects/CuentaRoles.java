package valueobjects;

public class CuentaRoles {
    private final String username;

    public CuentaRoles(String username) {
        this.username = username;
    }

    public String value(){
        return this.username;
    }
}
