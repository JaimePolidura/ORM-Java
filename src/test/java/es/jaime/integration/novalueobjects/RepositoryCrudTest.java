package es.jaime.integration.novalueobjects;

import org.testng.annotations.Test;

import java.util.UUID;

public final class RepositoryCrudTest {
    private static final String DB_LINK = "jdbc:mysql://localhost:3306/ormjavatest?user=root&password=&useSSL=false&allowPublicKeyRetrieval=true";

    private final Repository repository;

    public RepositoryCrudTest() {
        this.repository = new Repository(DB_LINK);
    }

    @Test
    public void shouldSave(){
        this.repository.save(new Model(UUID.randomUUID(),"key", "value"));
        this.repository.findAll().forEach(System.out::println);
    }
}
