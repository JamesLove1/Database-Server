package edu.uob;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.*;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.Comparator;
//test
class InterpTest {

    @BeforeEach
    void setupTestDB(){
        //create test DB
        String path = "testDatabases";
        File testDBFolder = new File(path);
        if(!testDBFolder.exists()){testDBFolder.mkdir();}
    }

    String quiry(String command) throws IOException, Errors {
        QuiryTokeniser tokens = new QuiryTokeniser(command);
        Interp interp = new Interp(tokens.getTokens(),"testDatabases");
        return interp.interpTokens();
    }

    @Test
    void testGetTokens() {
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add(0, "test");
        tokens.add(1,"test1");
        tokens.add(2,"test2");
        Interp inperCode = new Interp(tokens,"");
        assertEquals("test",inperCode.getTokens().get(0));
        assertEquals("test1",inperCode.getTokens().get(1));
        assertEquals("test2",inperCode.getTokens().get(2));
    }

    @Test
    void testValidCommand() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = Interp.class.getDeclaredMethod("validCommand", ArrayList.class);
        method.setAccessible(true);

        ArrayList<String> tokens = new ArrayList<>();
        Interp interpTest = new Interp(tokens,"");

        tokens.add(0, "USE");
        assertFalse((boolean) method.invoke(interpTest, tokens));

        interpTest.getTokens().set(0,"TEST");
        tokens.add(1, ";");
        assertFalse((boolean) method.invoke(interpTest, tokens));
        interpTest.getTokens().set(0,"test");
        assertFalse((boolean) method.invoke(interpTest, tokens));
        interpTest.getTokens().set(0,"USE");
        assertTrue((boolean) method.invoke(interpTest, tokens));
        interpTest.getTokens().set(0,"use");
        assertTrue((boolean) method.invoke(interpTest, tokens));
        interpTest.getTokens().set(0,"CREATE");
        assertTrue((boolean) method.invoke(interpTest, tokens));
        interpTest.getTokens().set(0,"create");
        assertTrue((boolean) method.invoke(interpTest, tokens));
        interpTest.getTokens().set(0,"DROP");
        assertTrue((boolean) method.invoke(interpTest, tokens));
        interpTest.getTokens().set(0,"drop");
        assertTrue((boolean) method.invoke(interpTest, tokens));
        interpTest.getTokens().set(0,"ALTER");
        assertTrue((boolean) method.invoke(interpTest, tokens));
        interpTest.getTokens().set(0,"alter");
        assertTrue((boolean) method.invoke(interpTest, tokens));
        interpTest.getTokens().set(0,"INSERT");
        assertTrue((boolean) method.invoke(interpTest, tokens));
        interpTest.getTokens().set(0,"insert");
        assertTrue((boolean) method.invoke(interpTest, tokens));
        interpTest.getTokens().set(0,"SELECT");
        assertTrue((boolean)method.invoke(interpTest,tokens));
        interpTest.getTokens().set(0,"select");
        assertTrue((boolean)method.invoke(interpTest,tokens));
        interpTest.getTokens().set(0,"UPDATE");
        assertTrue((boolean)method.invoke(interpTest,tokens));
        interpTest.getTokens().set(0,"update");
        assertTrue((boolean)method.invoke(interpTest,tokens));
        interpTest.getTokens().set(0,"DELETE");
        assertTrue((boolean)method.invoke(interpTest,tokens));
        interpTest.getTokens().set(0,"delete");
        assertTrue((boolean)method.invoke(interpTest,tokens));
        interpTest.getTokens().set(0,"JOIN");
        assertTrue((boolean)method.invoke(interpTest,tokens));
        interpTest.getTokens().set(0,"join");
        assertTrue((boolean)method.invoke(interpTest,tokens));
    }

    @Test
    void testUse() throws IOException, Errors {

        String command = "create database testDB ;";
        QuiryTokeniser tokens = new QuiryTokeniser(command);
        Interp interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "use testDB ;";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        assertEquals("[OK]",interp.interpTokens());

    }

    @Test
    void testCreate() throws IOException, Errors {
        String command = "create database testDB ;";
        QuiryTokeniser tokens = new QuiryTokeniser(command);
        Interp interp = new Interp(tokens.getTokens(),"testDatabases");
        assertEquals("[OK]",interp.interpTokens());

    }

    @Test
    void testCreateDatabase() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = Interp.class.getDeclaredMethod("createDatabase", ArrayList.class);
        method.setAccessible(true);

        ArrayList<String> tokens = new ArrayList<>();
        tokens.add(0, "CREATE");
        tokens.add(1, "DATABASE");
        tokens.add(2, "test");
        tokens.add(3, ";");

        Interp interpTest = new Interp(tokens, "/");

        //test Database
        assertEquals("[OK]",method.invoke(interpTest,tokens)); //need to edit this

        //test if methord will throw and error
        tokens.set(2, "t!#est");
        assertEquals("[ERROR] - Not Plaintext",method.invoke(interpTest,tokens));
    }

    @Test
    void testCreateTable() throws IOException, Errors {

        String command = "create database testDB ;";
        QuiryTokeniser tokens = new QuiryTokeniser(command);
        Interp interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "use testDB ;";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "create table testTable ( testcol , testCol1 , testCol2 );";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        assertEquals("[OK]",interp.interpTokens());

    }

    @Test
    void testDropTables () throws IOException, Errors {
        String command = "create database testDB ;";
        QuiryTokeniser tokens = new QuiryTokeniser(command);
        Interp interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "use testDB ;";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "create table testTable ( testcol , testCol1 , testCol2 );";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        assertEquals("[OK]",interp.interpTokens());

        command = "drop table testTable ;";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        assertEquals("[OK]",interp.interpTokens());
    }

    @Test
    void testDropDatabase() throws IOException, Errors {
        String command = "create database testDB ;";
        QuiryTokeniser tokens = new QuiryTokeniser(command);
        Interp interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "drop database testDB ;";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        assertEquals("[OK]",interp.interpTokens());
    }

    @Test
    void testInsert() throws IOException, Errors {

        String command = "create database testDB ;";
        QuiryTokeniser tokens = new QuiryTokeniser(command);
        Interp interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "use testDB ;";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "create table testTable ( testcol , testCol1 , testCol2 );";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "INSERT INTO testTable VALUES ( a , b , c );";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        assertEquals("[OK]",interp.interpTokens());
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

        command = "create table testTable ( testcol , testCol1 , testCol2 );";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        interp.interpTokens();

        command = "ALTER TABLE testTable ADD testColl ;";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        assertEquals("[OK]",interp.interpTokens());

        command = "ALTER TABLE testTable DROP testColl ;";
        tokens = new QuiryTokeniser(command);
        interp = new Interp(tokens.getTokens(),"testDatabases");
        assertEquals("[OK]",interp.interpTokens());

    }

    @Test
    void testSelect() throws IOException, Errors {

        quiry("create database testDB ;");
        quiry("use testDB ;");
        quiry("create table testTable ( testcol , testCol1 , testCol2 );");
        quiry("INSERT INTO testTable VALUES ( a , b , c ) ;");
        quiry("INSERT INTO testTable VALUES ( b , a , c ) ;");
        quiry("INSERT INTO testTable VALUES ( c , b , a ) ;");

        // test basic select commands
        assertTrue(quiry("SELECT * FROM testTable ;").contains("[OK]"));
        assertTrue(quiry("SELECT testcol , testCol1 FROM testTable;").contains("[OK]"));

        // test basic select commands for errors
        assertTrue(quiry("SELECT testcol , FROM testTable where ;").contains("[ERROR]"));
        assertTrue(quiry("SELECT testcol , , testCol1 FROM testTable ;").contains("[ERROR]"));

        // test complex commands
        assertTrue(quiry("SELECT * FROM testTable WHERE ;").contains("[ERROR]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE testcol == a ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE number <= 321 c ;").contains("[ERROR]")); // fix issue with c
        assertTrue(quiry("SELECT * FROM testTable WHERE testCol2 == c ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE testcol == a AND testCol2 == c ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE testcol == a OR testCol2 == c ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE (testcol == a) AND (testCol2 == c) ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE (testcol == a OR testCol2 == c) ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE (testcol == a OR testCol2 == c) AND (testcol == a OR testCol2 == c) ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE (testcol == a OR (testcol == a OR testCol2 == c))  ;").contains("[OK]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE ((testcol == a AND testCol2 == c) AND (testcol == a AND testCol2 == c)) ;").contains("[OK]"));

        // test for complex errors
        assertTrue(quiry("SELECT * FROM  testTable WHERE ;").contains("[ERROR]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE testcol == b AND ;").contains("[ERROR]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE ( testcol == b ;").contains("[ERROR]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE (testcol == a OR testCol2 == c ;").contains("[ERROR]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE (testcol == a OR (testcol == a OR testCol2 == c)  ;").contains("[ERROR]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE (testcol == a OR (testcol == a OR testCol2 == c)  ;").contains("[ERROR]"));
        assertTrue(quiry("SELECT * FROM testTable WHERE ((testcol == a AND testCol2 == c) AND (testcol == a AND testCol2 == c) ;").contains("[ERROR]"));
    }

    @Test
    void testUpdate() throws IOException, Errors {

        quiry("create database testDB ;");
        quiry("use testDB ;");
        quiry("create table testTable ( testcol , testCol1 , testCol2 );");
        quiry("INSERT INTO testTable VALUES ( a , b , c ) ;");
        quiry("INSERT INTO testTable VALUES ( b , a , c ) ;");
        quiry("INSERT INTO testTable VALUES ( c , b , a ) ;");

        // test basic select commands
        assertTrue(quiry("UPDATE testTable SET testcol = x WHERE  testcol == a ;").contains("[OK]"));
        assertTrue(quiry("UPDATE testTable SET testcol = y WHERE  testCol2 == c ;").contains("[OK]"));
        assertTrue(quiry("UPDATE testTable SET testcol = z WHERE  testCol2 == c AND testCol1 == b ;").contains("[OK]"));

        // test basic select commands for errors
        assertTrue(quiry(" testTable SET testcol = m WHERE  testcol == a ;").contains("[ERROR]"));
        assertTrue(quiry("UPDATE testTable  testcol = m WHERE  testcol == a ;").contains("[ERROR]"));
        assertTrue(quiry("UPDATE testTable SET testcol = m WHERE  testcol == a ").contains("[ERROR]"));
        assertTrue(quiry("UPDATE testTable SET testcol = m  testcol == a ;").contains("[ERROR]"));
        assertTrue(quiry("UPDATE  SET testcol = m WHERE  testcol == a ;").contains("[ERROR]"));
        assertTrue(quiry("UPDATE testTable SET testcol = j WHERE ;").contains("[ERROR]"));
        assertTrue(quiry("UPDATE testTable SET testcol = k WHERE   == a ;").contains("[ERROR]"));
        assertTrue(quiry("UPDATE testTable SET  = l WHERE  testcol == a ;").contains("[ERROR]"));
        assertTrue(quiry("UPDATE testTable WHERE SET  = l   testcol == a ;").contains("[ERROR]"));
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
        assertTrue(quiry("DELETE FROM testTable WHERE testcol == a ;").contains("[OK]"));
        assertTrue(quiry("DELETE FROM testTable WHERE testCol2 == c AND testCol1 == b ;").contains("[OK]"));

        // test basic select commands for errors
        assertTrue(quiry("DELETE  testTable WHERE testcol == a ;").contains("[ERROR]"));
        assertTrue(quiry("DELETE FROM  WHERE testcol == a ;").contains("[ERROR]"));
        assertTrue(quiry("DELETE FROM testTable  testcol == a ;").contains("[ERROR]"));

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
        quiry("INSERT INTO testTable VALUES ( 1 , 4235 ) ;");
        quiry("INSERT INTO testTable VALUES ( 2 , 435 ) ;");
        quiry("INSERT INTO testTable VALUES ( 3 , 45 ) ;");

        // test basic select commands
//        System.out.println(quiry("JOIN testTable AND testTable1 ON ID AND FK;"));
        assertTrue(quiry("JOIN testTable AND testTable1 ON ID AND FK;").contains("[OK]"));

        // test basic select commands for errors
        assertTrue(quiry(" testTable AND testTable1 ON ID AND FK;").contains("[ERROR]"));
        assertTrue(quiry("JOIN  AND testTable1 ON ID AND FK;").contains("[ERROR]"));
        assertTrue(quiry("JOIN testTable  testTable1 ON ID AND FK;").contains("[ERROR]"));
        assertTrue(quiry("JOIN testTable AND  ON ID AND FK;").contains("[ERROR]"));
        assertTrue(quiry("JOIN testTable AND testTable1  ID AND FK;").contains("[ERROR]"));
        assertTrue(quiry("JOIN testTable AND testTable1 ON  AND FK;").contains("[ERROR]"));
        assertTrue(quiry("JOIN testTable AND testTable1 ON ID  FK;").contains("[ERROR]"));
        assertTrue(quiry("JOIN testTable AND testTable1 ON ID AND ;").contains("[ERROR]"));

    }

    @Test
    void testCheckPlainText() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method method = Interp.class.getDeclaredMethod("checkPlainText", String.class);
        method.setAccessible(true);

        ArrayList<String> tokens = new ArrayList<>();
        tokens.add(0, "CREATE");
        tokens.add(1, "DATABASE");
        tokens.add(2, "test");
        tokens.add(3, ";");

        Interp interpTest = new Interp(tokens, "");

        //test lowercase characters
        assertEquals(true,method.invoke(interpTest,tokens.get(2))); //need to edit this

        //test uppercase characters
        tokens.set(2, "TEST");
        assertEquals(true,method.invoke(interpTest,tokens.get(2)));

        //test numbers
        tokens.set(2, "12345");
        assertEquals(true,method.invoke(interpTest,tokens.get(2)));

        //test uppercase, lowercase and numbers combined
        tokens.set(2, "TESTtest1234");
        assertEquals(true,method.invoke(interpTest,tokens.get(2)));

        //test if it picks up on ! character
        tokens.set(2, "!TEST");
        assertEquals(false,method.invoke(interpTest,tokens.get(2)));

        //test if it picks up on ! character
        tokens.set(2, "TE!S#T");
        assertEquals(false,method.invoke(interpTest,tokens.get(2)));
    }

    @Test
    void testCheckFloatLiteral() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method method = Interp.class.getDeclaredMethod("checkFloatLiteral", String.class);
        method.setAccessible(true);

        Interp interpTest = new Interp();
        String testStr = "10.0";

        //test decimal number
        assertEquals(true,method.invoke(interpTest,testStr));

        //test minus number
        testStr = "-5.5";
        assertEquals(true,method.invoke(interpTest,testStr));

        //test minus number
        testStr = "+3.43345";
        assertEquals(true,method.invoke(interpTest,testStr));

        //test 2 decimal points
        testStr = "+3.433.45";
        assertEquals(false,method.invoke(interpTest,testStr));

        //test letter
        testStr = "+3.4dfd45";
        assertEquals(false,method.invoke(interpTest,testStr));

        //test letter
        testStr = "+3.4d@";
        assertEquals(false,method.invoke(interpTest,testStr));
    }

    @Test
    void testCheckIntegerLiteral() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method method = Interp.class.getDeclaredMethod("checkIntegerLiteral", String.class);
        method.setAccessible(true);

        Interp interpTest = new Interp();
        String testStr = "10";

        //test decimal number
        assertEquals(true,method.invoke(interpTest,testStr));

        //test minus number
        testStr = "-5";
        assertEquals(true,method.invoke(interpTest,testStr));

        //test posertive number
        testStr = "+3";
        assertEquals(true,method.invoke(interpTest,testStr));

        //test symbol
        testStr = "3+";
        assertEquals(false,method.invoke(interpTest,testStr));

        //test symbol
        testStr = "3-";
        assertEquals(false,method.invoke(interpTest,testStr));

        //test letter
        testStr = "3.445";
        assertEquals(false,method.invoke(interpTest,testStr));

        //test symbol
        testStr = "3.4d@";
        assertEquals(false,method.invoke(interpTest,testStr));
    }

    @Test
    void testCheckStringLiteral() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method method = Interp.class.getDeclaredMethod("checkStringLiteral", String.class);
        method.setAccessible(true);

        Interp interpTest = new Interp();

        String testStr = "' '";
        assertEquals(true,method.invoke(interpTest,testStr));

        testStr = "'test'";
        assertEquals(true,method.invoke(interpTest,testStr));

        testStr = " ";
        assertEquals(true,method.invoke(interpTest,testStr));

        testStr = "abc";
        assertEquals(true,method.invoke(interpTest,testStr));

        testStr = "ABC";
        assertEquals(true,method.invoke(interpTest,testStr));

        testStr = "abcDEF";
        assertEquals(true,method.invoke(interpTest,testStr));

        testStr = "!#$%&()*+,-./:;>=<?@[]^_`{}~";
        assertEquals(true,method.invoke(interpTest,testStr));

        //test symbol
        testStr = "35363";
        assertEquals(true,method.invoke(interpTest,testStr));
    }

    @Test
    void testCheckBooleanLiteral() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method method = Interp.class.getDeclaredMethod("checkBooleanLiteral", String.class);
        method.setAccessible(true);

        Interp interpTest = new Interp();

        String testStr = "TRUE";
        assertEquals(true,method.invoke(interpTest,testStr));

        testStr = "FALSE";
        assertEquals(true,method.invoke(interpTest,testStr));

        testStr = "false";
        assertEquals(false,method.invoke(interpTest,testStr));

        testStr = "true";
        assertEquals(false,method.invoke(interpTest,testStr));

        testStr = "FALES";
        assertEquals(false,method.invoke(interpTest,testStr));

        testStr = "TREU";
        assertEquals(false,method.invoke(interpTest,testStr));
    }

    @Test
    void testCheckNULL() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method method = Interp.class.getDeclaredMethod("checkNULL", String.class);
        method.setAccessible(true);

        Interp interpTest = new Interp();

        String testStr = "NULL";
        assertEquals(true,method.invoke(interpTest,testStr));

        testStr = "FALSE";
        assertEquals(false,method.invoke(interpTest,testStr));
    }

    @AfterEach
    void deleteTestDB() throws IOException {
        String path = "testDatabases";
        Path path2 = Paths.get(path);
        Stream<Path> stream = Files.walk(path2);
//        System.out.println(stream);
        stream.sorted(Comparator.reverseOrder())
                .forEach(pathToDelete -> {
                    try {
                        Files.delete(pathToDelete);
//                        System.out.println("Deleted: " + pathToDelete);
                    } catch (IOException e) {
//                        System.err.println("Failed to delete: " + pathToDelete + " due to " + e.getMessage());
                    }
                });
    }


}