package edu.uob;

import java.io.*;
import java.security.PublicKey;

public class Database {

    static String dbname;

    private String storageFolderPath;

    public Database(String storageFolderPath) {
        this.storageFolderPath = storageFolderPath;
    }

    public String createDB(String databaseName) {
        String path = this.storageFolderPath+File.separator+databaseName;
        File newDB = new File(path);
        if(!newDB.exists()){
            newDB.mkdir();
            return "[OK]";
        }
        return "[ERROR] - createDB";
    }

    public String getDBName(){
        return Database.dbname;
    }

    public String setDBName(String use){
        String path = this.storageFolderPath+File.separator+use;
        File name = new File(path);
        if(name.exists()){
            Database.dbname = use;
            return "[OK]";
        }
        return "[ERROR] - setName";
    }

    public String dropDB(String databaseName) {
        String path = this.storageFolderPath+File.separator+databaseName;
        File DB = new File(path);
        if(DB.exists()){
            File[] files = DB.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            DB.delete();
            return "[OK]";
        }
        return "[ERROR] - dropDB";
    }


}
