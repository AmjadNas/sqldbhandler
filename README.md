# sqldbhandler
[![Maven Central](https://img.shields.io/maven-central/v/com.github.amjadnas/sql-db-handler.svg?label=Maven%20Central)](https://search.maven.org/artifact/com.github.amjadnas/sql-db-handler/1.0.0/jar%22)

This library was created as a side project. I decided to publish it in hopes of it being helpful to other developers. The library was originally inspired by the Room library published by Google for data persistence in Android. It aims to eliminate the boilerplate code that comes with parsing the query results. It also provides all the other operations (INSERT, UPDATE, DELETE).
### Installation
Maven:
```xml
<dependency>
    <groupId>com.github.amjadnas</groupId>
    <artifactId>sql-db-handler</artifactId>
    <version>1.0.0</version>
</dependency>
```
Gradle:
```
implementation 'com.github.amjadnas:sql-db-handler:1.0.0'
```
[JAR](https://repo1.maven.org/maven2/com/github/amjadnas/sql-db-handler/1.0.0/sql-db-handler-1.0.0.jar)
### [Javadocs](https://amjadnas.github.io/sqldbhandler/)
### Usage
#### Definning Entities
For every object that is used as a POJO it must be annotated as an @Entity and the name of the corresponding table in the database must be provided. Its fields must be annotated as @Column and the name of the corresponding column must be provided, if the field does not get annotated or its value is null, it will be ignored.
Also, a default constructor, setters and getters must be provided, otherwise when fetching the query results, the library will throw an exception.
For example:
```java
@Entity(name = "users", primaryKey = {"username"})
public class User {

    public User() {}    // must be provided

    @Column(name = "username")
    private String name;
    
    private String secondName;   // no coulemn annotation, field ignored.

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```
#### Definning DAOs (Data Access Object)
The DAO must be an interface and be annotated with @DAO. For defining queries in the interface, the user has to define methods and their return types based on what is needed, annotate them with @Query, @Insert, @Update, @Delete (check the documentation for more details on the annotationi types) and provide the query. For example:
```java
@Dao
public interface Dummy {

    @Query("SELECT * from movie limit 10")
    List<Movie> getAll(Connection connection, Object... whArgs);

    @Query("SELECT * from movie where id = ?")
    Movie getme(Connection connection, Object... whArgs);

    @Query("SELECT image from movie where id = ?")
    byte[] getImage(Connection connection, Object... whArgs);

    @Insert
    Movie insert(Connection connection, Movie move);

    @Update
    Movie update(Connection connection, Movie move);

    @Delete
    Movie delete(Connection connection, Movie move);

    @Delete(deleteBy = {"name"})
    int delete(Connection connection, Object...whargs);
}
```
Please keep in mind that the queries must be written to be used as prepared statement. With the "select" query type the defined methods must take (Connection connection, Object...whargs) as arguments.
#### Putting it all together
To initialize the handler the user needs to define a class annotated with @Handler, all of the DAOs must be fields in the handler and use the DBHanlder class to build the handler:
```java

@Handler(entities = {Movie.class, User.class})
public class MyHandler {

    private Dummy dummDao;

    public Dummy getDummDao() {
        return dummDao;
    }
}

```
After that the library takes care of implementing read/write operations.
```java
//build call DBHandler to build the defined handler
 MyHandler handler = DBHandler.build(MyHandler.class);
```
The defined the DAOs can be accessed by the getters defined in the handler.
```java
Dummy dummy =  handler.getDummDao();
```
#### Limitations
1) List is the only supported collection type for multiple results queries.
2) No query linting at compile time, meaning the developer has to make sure the query is syntactically correct.
