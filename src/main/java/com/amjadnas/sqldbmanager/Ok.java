package com.amjadnas.sqldbmanager;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class Ok  {

    public static void main(String[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, SQLException, ClassNotFoundException {
//        QueryHandler<List<User>> handler = FactoryQueryHandler.getHandler("list");

        // User user = new User("");



        DaoBuilder.buildDao(Dummy.class).getAll(null,"");

    }


}

