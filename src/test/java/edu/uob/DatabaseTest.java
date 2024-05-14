package edu.uob;
import java.lang.*;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {

    @Test
    void testCreateDB(){
        //create test DB
        String path = "testDatabases";
        String testDBPath = path+ File.separator+"test";
        File testDB = new File(path);
        if(!testDB.exists()){
            testDB.mkdir();
        }

        //test for creation of database
        Database databaseInstance = new Database(path);
        assertEquals("[OK]",databaseInstance.createDB("test"));

        //test to see if it will overwrite database
        Database databaseInstance1 = new Database(path);
        assertEquals("[ERROR] - createDB",databaseInstance1.createDB("test"));

        //Delete test DB
        File testDBtable = new File(path+File.separator+"test");
        if(testDBtable.exists()){ testDBtable.delete();}
        if(testDB.exists()){ testDB.delete();}
    }

    @Test
    void testGetName() {
        //create test DB
        String path = "testDatabases";
        File testDBFolder = new File(path);
        if(!testDBFolder.exists()){testDBFolder.mkdir();}
        String testDBPath = path+ File.separator+"test";
        File testDB = new File(testDBPath);
        if(!testDB.exists()){testDB.mkdir();}

        //set static varaible using setName
        Database database = new Database(path);
        assertEquals("[OK]",database.setDBName("test"));

        // test it using static assesors
        assertEquals("test", Database.dbname);

        // test getter methord
        assertEquals("test",database.getDBName());

        // test asessable from another object
        Database database1 = new Database(path);
        assertEquals("test",database1.getDBName());

        //Delete test DB
        if(testDB.exists()){ testDB.delete();}
        if(testDBFolder.exists()){ testDBFolder.delete();}
    }

    @Test
    void testSetName() {
        //create test DB
        String path = "testDatabases";
        File testDBFolder = new File(path);
        if(!testDBFolder.exists()){testDBFolder.mkdir();}
        String testDBPath = path+ File.separator+"test";
        File testDB = new File(testDBPath);
        if(!testDB.exists()){testDB.mkdir();}

        //set static varaible using setName
        Database database = new Database(path);
        assertEquals("[OK]",database.setDBName("test"));
        // test it using static assesors
        assertEquals("test", Database.dbname);

        //Delete test DB
        if(testDB.exists()){ testDB.delete();}
        if(testDBFolder.exists()){ testDBFolder.delete();}
    }

        @Test
    void testDropDB(){
        //create test DB
        String path = "testDatabases";
        File testDBFolder = new File(path);
        if(!testDBFolder.exists()){testDBFolder.mkdir();}
        String testDBPath = path+ File.separator+"test";
        File testDB = new File(testDBPath);
        if(!testDB.exists()){testDB.mkdir();}
        Database databaseInstance = new Database(path);

        //test for deleting of database
        assertEquals("[OK]",databaseInstance.dropDB("test"));

        //test for deleting Database that does not exsist
        assertEquals("[ERROR] - dropDB" ,databaseInstance.dropDB("test"));

        //Delete test DB
        if(testDB.exists()){ testDB.delete();}
        if(testDBFolder.exists()){ testDBFolder.delete();}
    }

}