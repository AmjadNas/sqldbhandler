package com.amjadnas.sqldbmanager;

import java.util.List;

public class Ok {

    public static void main(String[] args) {
        @SuppressWarnings("unchecked")
        QueryHandler<List<User>> handler = FactoryQueryHandler.getHandler("list");

    }
}


