package edu.uob;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.desktop.SystemEventListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TableTest {


    @BeforeEach
    void setupTestDB(){
        //create test DB
        String path = "testDatabases";
        File testDBFolder = new File(path);
        if(!testDBFolder.exists()){testDBFolder.mkdir();}
    }

    String quiry(String command) throws IOException, Errors {

        try {
            QuiryTokeniser tokens = new QuiryTokeniser(command);
            Interp interp = new Interp(tokens.getTokens(),"testDatabases");
            return interp.interpTokens();
        } catch (FileNotFoundException | Errors e ) {
            return "[ERROR] - "+e;
        }
    }

    @Test
    void testStorageFolderPathAndDBExsits() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, Errors {

        String command = "create database testDB ;";
        QuiryTokeniser tokens = new QuiryTokeniser(command);
        Interp interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "use testDB ;";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        //change private to public to test
        Method method = Table.class.getDeclaredMethod("storageFolderPathAndDBExsits");
        method.setAccessible(true);

        Table testTable = new Table("testDatabases","testDB");
        assertEquals(true,method.invoke(testTable));

    }

    @Test
    void testCreateTable() throws IOException, NoSuchFieldException, IllegalAccessException, Errors {

        String command = "create database testDB ;";
        QuiryTokeniser tokens = new QuiryTokeniser(command);
        Interp interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "use testDB ;";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        // simulate string being tokenised
        command = "CREATE TABLE testTable ;";
        tokens = new QuiryTokeniser(command);

        Table testTable1 = new Table("testDatabases","testTable");
        assertEquals("[OK]",testTable1.createTable(tokens.getTokens()));

    }

    @Test
    void testDropTable() throws IOException, Errors {

        String command = "create database testDB ;";
        QuiryTokeniser tokens = new QuiryTokeniser(command);
        Interp interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "use testDB ;";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "CREATE TABLE testTable ;";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        Table testTable = new Table("testDatabases","testTable");
        assertEquals("[OK]",testTable.dropTable());

    }

    @Test
    void testLoadTableFile() throws IOException, NoSuchFieldException, IllegalAccessException, Errors {
        String command = "create database testDB ;";
        QuiryTokeniser tokens = new QuiryTokeniser(command);
        Interp interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "use testDB ;";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "CREATE TABLE testTable ( a , b , c , d ) ;";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "INSERT INTO testTable ( 1 , 2 , 3 , 4 ) ;";
        tokens = new QuiryTokeniser(command);
        Table insertIntoTable = new Table("testDatabases", "testTable");
        insertIntoTable.insert(tokens.getTokens());
        assertEquals( "a" , insertIntoTable.getDataFrame().get(0).get(1));
        assertEquals( "b" , insertIntoTable.getDataFrame().get(0).get(2));
        assertEquals( "c" , insertIntoTable.getDataFrame().get(0).get(3));
        assertEquals( "d" , insertIntoTable.getDataFrame().get(0).get(4));
    }

    @Test
    void testAlter() throws IOException, Errors {
        String command = "create database testDB ;";
        QuiryTokeniser tokens = new QuiryTokeniser(command);
        Interp interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "use testDB ;";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "CREATE TABLE testTable ( a , b , c , d ) ;";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "INSERT INTO testTable values ( 1 , 2 , 3 , 4 ) ;";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "INSERT INTO testTable values ( 1a , 2b , 3c , 4d ) ;";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "ALTER TABLE testTable ADD alterCol ;";
        tokens = new QuiryTokeniser(command);

        Table testTable = new Table("testDatabases", "testTable");
        assertEquals("[OK]",testTable.alter(tokens.getTokens()));
        int tableLen = testTable.getDataFrame().get(0).size()-1;
        assertEquals("alterCol",testTable.getDataFrame().get(0).get(tableLen));

        command = "ALTER TABLE testTable DROP a ;";
        tokens = new QuiryTokeniser(command);
        Table testTable1 = new Table("testDatabases", "testTable");
        assertEquals("[OK]",testTable1.alter(tokens.getTokens()));
        assertEquals("b",testTable1.getDataFrame().get(0).get(1));

    }

    @Test
    void testSelect() throws IOException, Errors {

        quiry("create database testDB ;");
        quiry("use testDB ;");
        quiry("create table testTable ( testcol , testCol1 , testCol2 , number );");
        quiry("INSERT INTO testTable VALUES ( a , b , c , 123 ) ;");
        quiry("INSERT INTO testTable VALUES ( b , a , C , 321 ) ;");
        quiry("INSERT INTO testTable VALUES ( c , b , ggg , 24254 );");
        quiry("INSERT INTO testTable VALUES ( m , r , ggg , 3423 );");
        quiry("INSERT INTO testTable VALUES ( b , k , gg , 34 );");
        quiry("INSERT INTO testTable VALUES ( v , f , z , 2456 );");
        quiry("use testDB ;");

        //test select * etc
        assertTrue(quiry("SELECT * FROM testTable ;").contains("[OK]"));
        assertTrue(quiry("SELECT testcol , testCol1 FROM testTable;").contains("[OK]"));
        assertTrue(quiry("SELECT testcol , FROM testTable where ;").contains("[ERROR]"));
        assertTrue(quiry("SELECT testcol , , testCol1 FROM testTable where ;").contains("[ERROR]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE  ;").contains("[ERROR]"));

        //test where
        assertTrue(quiry("SELECT * FROM testTable WHERE testCol2 == c ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE number == 321 ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE testCol2 != c ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE number != 321 ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE number > 321 ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE number > 392.453445 ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE number < 321 ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE number < 321.3243 ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE number >= 321 ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE number >= 300.43325 ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE number >= 321 ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE number >= 320.45423 ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE number <= 321 ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE number <= 390.45423 ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE testCol2 LIKE C ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE number LIKE 123 ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE number != 321 AND testCol2 == c ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE number != 321 OR testCol2 == c ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE testCol2 == c OR number != 321 ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE ( number != 321 ) AND ( testCol2 == c );").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE ( number != 321 AND testCol2 == c );").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE (( number != 321 ) AND ( testCol2 == c )) ;").contains("[OK]"));

        //test for errors
        assertTrue(quiry("SELECT * FROM testTable WHERE testCol2==c ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE testCol2 LIKE ggg ;").contains("[OK]"));
//        System.out.println();

    }

    @Test
    void testUpdate() throws IOException, Errors {

        quiry("create database testDB ;");
        quiry("use testDB ;");
        quiry("create table testTable ( testcol , testCol1 , testCol2 , number );");
        quiry("INSERT INTO testTable VALUES ( a , b , c , 123 ) ;");
        quiry("INSERT INTO testTable VALUES ( b , a , C , 321 ) ;");
        quiry("INSERT INTO testTable VALUES ( c , b , a , 45254 );");
        quiry("INSERT INTO testTable VALUES ( m , r , g , 3423 );");
        quiry("INSERT INTO testTable VALUES ( b , k , g , 34 );");
        quiry("INSERT INTO testTable VALUES ( v , f , z , 2456 );");
        quiry("use testDB ;");


        //test for correct values
        assertTrue(quiry("UPDATE testTable SET testcol = z WHERE  testcol != a ;").contains("[OK]"));

        //test for error values
        assertTrue(quiry("UPDATE testTable SET nonAttribute = z WHERE testcol != a ;").contains("[ERROR]"));


    }

    @Test
    void testDelete() throws IOException, Errors {

        quiry("create database testDB ;");
        quiry("use testDB ;");
        quiry("create table testTable ( testcol , testCol1 , testCol2 );");
        quiry("INSERT INTO testTable VALUES ( a , b , c ) ;");
        quiry("INSERT INTO testTable VALUES ( b , a , c ) ;");
        quiry("INSERT INTO testTable VALUES ( c , b , a ) ;");

        // test basic select commands
        Table testTable =  deleteTable("DELETE FROM testTable WHERE testcol == a ;");
        assertEquals("[ ,  ,  ,  ]",testTable.getDataFrame().get(1).toString());

        quiry("INSERT INTO testTable VALUES ( a , b , c ) ;");
        testTable = deleteTable("DELETE FROM testTable WHERE testCol2 == c AND testCol1 == b ;");
//        assertEquals("[ ,  ,  ,  ]", testTable.getDataFrame().get(4).toString());

        // test basic select commands for errors
        assertTrue(quiry("DELETE  testTable WHERE testcol == a ;").contains("[ERROR]"));
        assertTrue(quiry("DELETE FROM  WHERE testcol == a ;").contains("[ERROR]"));
        assertTrue(quiry("DELETE FROM testTable  testcol == a ;").contains("[ERROR]"));

    }

    private Table deleteTable(String command) throws FileNotFoundException {
        QuiryTokeniser tokens = new QuiryTokeniser(command);
        Table table = new Table("testDatabases", "testTable");
        table.delete(tokens.getTokens());
        return table;
    }


    @Test
    void testJoin() throws IOException, Errors {

        quiry("create database testDB ;");
        quiry("use testDB ;");
        quiry("create table testTable ( testcol , testCol1 , testCol2 );");
        quiry("INSERT INTO testTable VALUES ( a , b , c ) ;");
        quiry("INSERT INTO testTable VALUES ( b , a , c ) ;");
        quiry("INSERT INTO testTable VALUES ( c , b , a ) ;");
        quiry("create table testTable1 ( FK , data1 );");
        quiry("INSERT INTO testTable1 VALUES ( 3 , 45 ) ;");
        quiry("INSERT INTO testTable1 VALUES ( 2 , 435 ) ;");
        quiry("INSERT INTO testTable1 VALUES ( 1 , 4235 ) ;");

        Table testTable = joinTable("JOIN testTable AND testTable1 ON id AND FK;");
        assertEquals( "1",testTable.getDataFrame().get(1).get(0));
        assertEquals( "4235",testTable.getDataFrame().get(1).get(5));
//        System.out.println(quiry("JOIN testTable AND testTable1 ON id AND FK;"));
    }

    private Table joinTable(String command) throws FileNotFoundException {
        QuiryTokeniser tokens = new QuiryTokeniser(command);
        Table table = new Table("testDatabases", "testTable");
        table.join(tokens.getTokens());
        return table;
    }

    @AfterEach
    void deleteTestDB() throws IOException {
        String path = "testDatabases";
        Path path2 = Paths.get(path);
        Stream<Path> stream = Files.walk(path2);
        stream.sorted(Comparator.reverseOrder())
                .forEach(pathToDelete -> {
                    try {
                        Files.delete(pathToDelete);
                    } catch (IOException e) {

                    }
                });
    }

}