package com.example.reweb.ulti.Config;

public class Config {

    public static final String DB_NAME="t2008m-webservice";
    public static final String IS_CREATE="?createDatabaseIfNotExist=true";
    public static final String DB_URL="jdbc:mysql://localhost:3306/"+DB_NAME+IS_CREATE;
    public static final String DB_USERNAME="root";
    public static final String DB_PASSWORD="";
    public static final String DB_DRIVER_CARD="com.mysql.jdbc.Driver";
}
