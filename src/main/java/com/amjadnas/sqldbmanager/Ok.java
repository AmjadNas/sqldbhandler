package com.amjadnas.sqldbmanager;

import com.amjadnas.sqldbmanager.builder.DBHandler;
import test.Dummy;
import test.MyHandler;
import test.User;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class Ok  {

    public static void main(String[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, SQLException, ClassNotFoundException {
//        QueryHandler<List<User>> handler = FactoryQueryHandler.getHandler("list");

        // User user = new User("");


        MyHandler handler = DBHandler.build(MyHandler.class);
        Dummy dummy =  handler.getDummDao();
        List<User> users = dummy.getAll(null,"");
        //System.out.println(users.get(0).getName());
        //System.out.println(dummy.getme(null,""));
    }


}

