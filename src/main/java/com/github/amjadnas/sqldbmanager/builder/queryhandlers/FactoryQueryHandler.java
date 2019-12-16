package com.github.amjadnas.sqldbmanager.builder.queryhandlers;

public final class FactoryQueryHandler {

    private FactoryQueryHandler(){}

    public static <T> QueryHandler getHandler(String returnType){

        if (returnType.equals("list")){
            return new ListQueryHandler<T>();
        }else {
            return new SingleObjectQueryHandler<T>();
        }

    }
}
