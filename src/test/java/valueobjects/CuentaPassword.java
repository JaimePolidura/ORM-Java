package valueobjects;

public class CuentaPassword {
    private final String username;

    public CuentaPassword(String username) {
        this.username = username;
    }

    public String value(){
        return this.username;
    }
}
