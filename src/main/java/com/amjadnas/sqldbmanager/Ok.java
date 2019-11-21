package com.amjadnas.sqldbmanager;

import com.amjadnas.sqldbmanager.builder.DBHandler;
import test.*;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Ok  {

    public static void main(String[] args)  throws SQLException {
//        QueryHandler<List<User>> handler = FactoryQueryHandler.getHandler("list");

        // User user = new User("");



        MyHandler handler = DBHandler.build(MyHandler.class);


        Dummy dummy =  handler.getDummDao();
       // try(Connection connection = DBManager.getInstance().getConnection()){
        //    List<Movie> users = dummy.getAll(connection);

          //  users.forEach(System.out::println);

           // Movie movie = dummy.getme(connection, 1);
            Movie movie1 = new Movie(1, "aa", null, null, 2);



           System.out.println(dummy.insert(null,movie1));
      //  }
        //System.out.println(users.get(0).getName());
        //System.out.println(dummy.getme(null,""));
    }


}

