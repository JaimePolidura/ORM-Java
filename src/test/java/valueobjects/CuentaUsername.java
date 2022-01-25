package valueobjects;

public class CuentaUsername {
    private final String username;

    public CuentaUsername(String username) {
        this.username = username;
    }

    public String value(){
        return this.username;
    }
}
