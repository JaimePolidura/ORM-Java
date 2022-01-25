package plain;

import lombok.Data;

@Data
public class Cuenta {
    private final int id;
    private final String username;
    private final String password;
    private final int active;
    private final String roles;
}
