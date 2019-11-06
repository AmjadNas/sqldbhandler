package com.amjadnas.sqldbmanager;

import java.util.List;

public final class QueryBuilder {

    private QueryBuilder() {}

    public static String insertQuery(String tableName, List<Pair> pairs) {

        int i = 0;

        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(" VALUES (");
        stringBuilder.append("INSERT INTO ")
                .append(tableName)
                .append(" (");

        for (Pair p : pairs) {
            if (i < pairs.size() - 1) {
                stringBuilder2.append("?,");
                stringBuilder.append(p.first)
                        .append(",");
            } else {
                stringBuilder2.append("?)");
                stringBuilder.append(p.first)
                        .append(")");
            }
            i++;
        }

        return stringBuilder.toString().concat(stringBuilder2.toString());
    }

    public static String updateQuery(String tableName, String primary, Pair...pairs){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("UPDATE ")
                .append(tableName)
                .append(" SET ");
        int i = 0;

        for (Pair p : pairs) {
            if (i < pairs.length - 1) {
                stringBuilder.append(p.first)
                        .append("=?, ");
            } else {
                stringBuilder.append(p.first)
                        .append("=?");
            }
            i++;
        }

        stringBuilder.append(" WHERE ")
                .append(primary)
                .append("=?;");

       return stringBuilder.toString();
    }

    public static String deleteQuery(String tableName, String primary){
        String delete = "DELETE FROM "
                .concat(tableName)
                .concat(" WHERE ")
                .concat(primary)
                .concat("=?;");

        return delete;
    }

}
