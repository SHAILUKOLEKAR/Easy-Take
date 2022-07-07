package com.shailu.eteasytake.model;

public class Categery
{
    private String Table_No;


    public Categery() {
    }

    public Categery(String table_No, String user_id) {
        Table_No = table_No;

    }

    public String getTable_No() {
        return Table_No;
    }

    public void setTable_No(String table_No) {
        Table_No = table_No;
    }


}
