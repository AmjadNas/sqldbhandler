package test;

import com.github.amjadnas.sqldbmanager.builder.DBHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Ok  {

    public static void main(String[] args)  throws SQLException {
//        QueryHandler<List<User>> handler = FactoryQueryHandler.getHandler("list");

        // User user = new User("");



        MyHandler handler = DBHandler.build(MyHandler.class);


        Dummy dummy =  handler.getDummDao();
        try(Connection connection = DBManager.getInstance().getConnection()){



           // List<Movie> users = dummy.getAll(connection);

          //  users.forEach(System.out::println);

           // Movie movie = dummy.getme(connection, 1);
           // Movie movie1 = new Movie(null, "aa", Category.action, Time.valueOf("1:50:00"), 2);

            long start = System.nanoTime();
            List<Movie> users = dummy.getAll(connection);
          //System.out.println(dummy.insert(connection,movie1));
           System.out.println(users);
            users.get(0).setName("Gone by the bit");
           System.out.println(dummy.update( connection,users.get(0)));

        }
        //System.out.println(users.get(0).getName());
        //System.out.println(dummy.getme(null,""));
    }


}

