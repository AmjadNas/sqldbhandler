package com.github.amjadnas.sqldbmanager.builder.queryhandlers;
@Deprecated(since = "0.1.0")
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
