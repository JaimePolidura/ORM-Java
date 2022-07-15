package es.jaime.integration.novalueobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public final class Model {
    @Getter private UUID id;
    @Getter private final String keymodel;
    @Getter private final String valuemodel;
    @Getter private final List<String> lista;

    @Override
    public String toString() {
        return "Model{" +
                "id=" + id +
                ", key='" + keymodel + '\'' +
                ", value='" + valuemodel + '\'' +
                '}';
    }
}
