package valueobjects;

public class CuentaId {
    private final int value;

    public CuentaId(int value) {
        this.value = value;
    }

    public int value(){
        return this.value;
    }
}
