import lombok.Data;

@Data
public class Cuenta {
    private final CuentaId id;
    private final CuentaUsername username;
    private final CuentaPassword password;
    private final CuentaActive active;
    private final CuentaRoles roles;

    public static Cuenta create(int id, String username, String password, int active, String roles){
        return new Cuenta(new CuentaId(id), new CuentaUsername(username), new CuentaPassword(password), new CuentaActive(active), new CuentaRoles(roles));
    }
}
