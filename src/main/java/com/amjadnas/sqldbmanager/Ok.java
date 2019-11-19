package com.amjadnas.sqldbmanager;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class Ok  {

    public static void main(String[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, SQLException, ClassNotFoundException {
//        QueryHandler<List<User>> handler = FactoryQueryHandler.getHandler("list");

        // User user = new User("");



        Dummy dummy =  DaoBuilder.buildDao(Dummy.class);
        List<User> users =dummy.getAll(null,"");
        System.out.println(users);
        dummy.getme(null,"");
    }


}

