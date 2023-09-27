# ORM-Java

### Set up
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.JaimePolidura</groupId>
    <artifactId>ORM-Java</artifactId>
    <version>2.1.3</version>
</dependency>
```

### Basic usage

```java
MySQLUsersRepository repository = new MySQLUsersRepository(new ConnectionManager(new MySQLConfiguration()));
repository.save(new User("Jaime"));

//Generic parameters: Repository type class, ID type class, Ignored for now
class MySQLUsersRepository extends Repository<User, UUID, Object> {
    public MySQLUsersRepository(ConnectionManager connectionManager) {
        super(connectionManager);
    }
    
    //Used for inserts & updates
    public void save(User user) {
        super.save(user);
    }

    public List<User> findSinceLastLoginTime(LocalDateTime lastLoginTime) {
        //Or return super.buildListFromQuery(String.format("SELECT * FROM users WHERE lastLoginTime >= %s", lastLoginTime));
        return super.buildListFromQuery(
                Select.from("users")
                        .where("lastLoginTime")
                        .biggerOrEqual(lastLoginTime)
        );
    }
    
    public Optional<User> findByName(String name) {
        return super.buildObjectFromQuery(Select.from("users").where("name").equal(name));
    }
    
    public void deleteById(UUID userId) {
        super.execute(Delete.from("users").where("userId").equal(userId));
        //Or super.deleteById(userId); 
    }

    @Override
    public EntityMapper<User, Object> entityMapper() {
        return EntityMapper.builder()
                .classesToMap(User.class)
                .idField("userId")
                .table("users")
                .build();
    }
}

class MySQLConfiguration extends DatabaseConfiguration {
    @Override
    public String url() {
        return "<your database jdbc connection url>";
    }
}

class User {
    private UUID userId;
    private String name;
    private LocalDateTime creationTime;
    private LocalDateTime lastLoginTime;
}
```
### Configuration

```java
class MySQLConfiguration extends DatabaseConfiguration {
    @Override
    public String url() {
        return "<your database jdbc connection url>";
    }
    
    @Override
    public boolean showQueries() {
        //Log queries
    }

    @Override
    public ConnectionPool connectionPool() {
        //Default: PerThreadConnectionPool. Other option is SharedConnectionPool
        //You can implement your own ConnectionPool by implementing ConnectionPool interface and returning it here
    }

    @Override
    public List<String> getCommandsToRun() {
        //Initial commands to run
    }
    
    @Override
    public ObjectMapper objectMapper() {
        //Custom jackson object mapper
    }
}
```
```java
ORMJava.addCustomSerializer(User.class, (User user) -> "return user json"); 
ORMJava.addCustomDeserializer(User.class, (String userJson) -> new User() /*"return object of user given the json"*/);

class User {
    private UUID userId;
    private String name;
}
```
### Conditional mappers

```java
class MySQLUsersRepository extends Repository<User, UUID, UserType> {
    public <T extends User> void save(T user) {
        super.save(user);
    }

    //You can cast it later
    public List<User> findByType(UserType type) {
        return super.buildListFromQuery(
                Select.from("users").where("type").equal(type)
        );
    }

    @Override
    public EntityMapper<User, UserType> entityMapper() {
        return EntityMapper.builder()
                .classesToMap(User.class)
                .idField("userId")
                .table("users")
                .conditionalClassMapping(ConditionalClassMapping.<User, UserType>builder()
                        .typeValueAccessor(resultSet -> UserType.valueOf(resultSet.getString("type")))
                        .typeClass(UserType.class)
                        .entitiesTypeMapper(Map.of(
                                UserType.NORMAL, NormalUser.class,
                                UserType.ADMIN, AdminUser.class
                        ))
                        .build())
                .build();
    }
}

enum UserType {
    NORMAL,
    ADMIN
}

abstract class User {
    private UUID userId;
    private UserType type;
}

class NormalUser extends User { }

class AdminUser extends User {
    private UUID grantedAdminByUserId;
}
```
