package es.jaime.integration.novalueobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public final class Model {
    @Getter private final UUID id;
    @Getter private final String keymodel;
    @Getter private final String valuemodel;

    @Override
    public String toString() {
        return "Model{" +
                "id=" + id +
                ", key='" + keymodel + '\'' +
                ", value='" + valuemodel + '\'' +
                '}';
    }
}
