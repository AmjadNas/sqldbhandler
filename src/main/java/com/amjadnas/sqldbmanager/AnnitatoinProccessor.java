package com.amjadnas.sqldbmanager;

import com.amjadnas.sqldbmanager.annotations.Entity;
import com.amjadnas.sqldbmanager.annotations.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AnnitatoinProccessor {

    static <T> void proccess(Connection connection, T user) throws SQLException {

        Class<?> obj = user.getClass();
        int i = 1;
        Entity entityAnnot = obj.getAnnotation(Entity.class);

        List<Pair> pairs = ClassHelper.getFields(user);

        String insert = QueryBuilder.insertQuery(entityAnnot.name(), pairs);
        try (PreparedStatement preparedStatement = connection.prepareStatement(insert)) {

            for (Pair p : pairs) {
                preparedStatement.setObject(i, p.second);
                i++;
            }

        }


    }

    @Query("SELECT * FROM blabal WHERE x = {id}")
    public static void query(int id){

    }


}
