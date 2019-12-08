package com.amjadnas.sqldbmanager.utills;

import java.util.List;

public final class QueryBuilder {

    private QueryBuilder() {
    }

    public static String insertQuery(String tableName, List<Pair<String, Object>> pairs) {

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

    public static String updateQuery(String tableName, String[] primary, List<Pair<String, Object>> pairs) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("UPDATE ")
                .append(tableName)
                .append(" SET ");

        int i = 0;
        int length = pairs.size();
        for (Pair p : pairs) {
            if (i < length - 1) {
                stringBuilder.append(p.first)
                        .append("=?, ");
            } else {
                stringBuilder.append(p.first)
                        .append("=?");
            }
            i++;
        }

        i = 0;
        stringBuilder.append(" WHERE ");
        for (String key : primary) {
            if (i < primary.length - 1) {
                stringBuilder.append(key)
                        .append("=? ")
                        .append(" AND ");
            } else {
                stringBuilder.append(key)
                        .append("=?");
            }
            i++;
        }
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    public static String deleteQuery(String tableName, String[] primary) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("DELETE FROM ")
                .append(tableName)
                .append(" WHERE ");
        int i = 0;
        for (String key : primary) {
            if (i < primary.length - 1) {
                stringBuilder.append(key)
                        .append("=? ")
                        .append(" AND ");
            } else {
                stringBuilder.append(key)
                        .append("=?");
            }
            i++;

        }
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

}
