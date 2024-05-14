package edu.uob.extraTest;

import edu.uob.DBServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TestsFromExampleWordDoc {
    DBServer server;
    
    @BeforeEach
    public void setup() {
        // Set up the server and clear out the database directory
        server = new DBServer();
//        try {
//            dropAll();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }


    @AfterEach
    void deleteTestDBContents() throws IOException {
        String directoryPath = "databases/";
        Path rootPath = Paths.get(directoryPath);

        try (Stream<Path> paths = Files.walk(rootPath)) {
            // Sort in reverse order to delete contents before directories
            paths.sorted(Comparator.reverseOrder())
                    .filter(path -> !path.equals(rootPath)) // Exclude the root directory itself
                    .forEach(pathToDelete -> {
                        try {
                            Files.delete(pathToDelete);
                        } catch (IOException e) {
                            // Consider logging the exception if needed
                        }
                    });
        } // try-with-resources will auto-close the stream
    }

    private String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000),
            () -> { return server.handleCommand(command);},
            "Server took too long to respond (probably stuck in an infinite loop)");
    }
    
    @Test
    public void exampleTest_1() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
    }
    
    @Test
    public void exampleTest_2() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
    }
    
    @Test
    public void exampleTest_3() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
    }
    
    @Test
    public void exampleTest_4() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +"65, TRUE);").contains("[OK]"));
    }
    
    @Test
    public void exampleTest_5() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +"65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +"55, TRUE);").contains("[OK]"));
    }
    
    @Test
    public void exampleTest_6() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +"65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +"55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +"35, FALSE);").contains("[OK]"));
    }
    
    @Test
    public void exampleTest_7() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +"65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +"55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +"35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +"20, FALSE);").contains("[OK]"));
    }
    
    @Test
    public void exampleTest_8() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +"65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +"55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +"35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +"20, FALSE);").contains("[OK]"));
//        String output = sendCommandToServer("SELECT * FROM marks ;");
        assertEquals("[OK]\n" +
                        "id    name             mark             pass             \n" +
                        "1     'Steve'          65               TRUE             \n" +
                        "2     'Dave'           55               TRUE             \n" +
                        "3     'Bob'            35               FALSE            \n" +
                        "4     'Clive'          20               FALSE            \n"
                , sendCommandToServer("SELECT * FROM marks;"));
    }


    @Test
    public void exampleTest_9() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " + "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', "+"55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +"35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +"20, FALSE);").contains("[OK]"));
        assertEquals("[OK]\n" +
                        "id    name             mark             pass             \n" +
                "1     'Steve'          65               TRUE             \n" +
                "3     'Bob'            35               FALSE            \n" +
                "4     'Clive'          20               FALSE            \n"
                , sendCommandToServer("SELECT * FROM " +"marks WHERE name != 'Dave';"));
    }
    
    @Test
    public void exampleTest_10() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +"65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +"55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +"35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +"20, FALSE);").contains("[OK]"));
        assertEquals("[OK]\n" +
                "id    name             mark             pass             \n" +
                "1     'Steve'          65               TRUE             \n" +
                "2     'Dave'           55               TRUE             \n"
                , sendCommandToServer("SELECT * FROM " +
            "marks WHERE pass == TRUE;"));
    }
    
    @Test
    public void exampleTest_11() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +"65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " + "55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +"35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +"20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +"submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('STAG', 2);").contains("[OK]"));
        assertEquals("[OK]\n" +
                        "id    task             submission       \n" +
                        "1     'OXO'            3                \n" +
                        "2     'DB'             1                \n" +
                        "3     'OXO'            4                \n" +
                        "4     'STAG'           2                \n"
                , sendCommandToServer("SELECT * FROM coursework;"));
    }
    
    @Test
    public void exampleTest_12() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +"65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +"55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +"35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +"20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +"submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('STAG', 2);").contains("[OK]"));
        assertEquals("[OK]\n" +
                        "ID    coursework.task       coursework.submission  marks.name            marks.mark            marks.pass            \n" +
                        "1     'OXO'                 3                     'Bob'                 35                    FALSE                 \n" +
                        "2     'DB'                  1                     'Steve'               65                    TRUE                  \n" +
                        "3     'OXO'                 4                     'Clive'               20                    FALSE                 \n" +
                        "4     'STAG'                2                     'Dave'                55                    TRUE                  \n",
                sendCommandToServer("JOIN coursework AND marks "+"ON submission AND id;"));
    }
    
    @Test
    public void exampleTest_13() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +"65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +"55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +"35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +"20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +"submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +"name == 'Clive';").contains("[OK]"));
    }
    
    @Test
    public void exampleTest_14() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +"65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +"55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +"35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +"20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +"submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +"name == 'Clive';").contains("[OK]"));
        assertEquals("[OK]\n" +
                        "id    name             mark             pass             \n" +
                        "4     'Clive'          38               FALSE            \n"
                ,sendCommandToServer("SELECT * FROM marks WHERE name == 'Clive';"));
    }
    
    @Test
    public void exampleTest_15() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +"65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +"55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +"35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +"20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +"submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +"name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +"'Dave';").contains("[OK]"));
    }
    
    @Test
    public void exampleTest_16() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +"65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +"55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +"35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +"20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +"submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +"name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +"'Dave';").contains("[OK]"));
        assertEquals("[OK]\n" +
                        "id    name             mark             pass             \n" +
                        "1     'Steve'          65               TRUE             \n" +
                        "                                                         \n" +
                        "3     'Bob'            35               FALSE            \n" +
                        "4     'Clive'          38               FALSE            \n"
                , sendCommandToServer("SELECT * FROM marks;"));
    }
    
    @Test
    public void exampleTest_17() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +"65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +"55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +"35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +"20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +"submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +"name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +"'Dave';").contains("[OK]"));
        assertEquals("[OK]\n" +
                        "id    name             mark             pass             \n" +
                        "4     'Clive'          38               FALSE            \n",
                sendCommandToServer("SELECT * FROM marks WHERE (pass == FALSE) AND (mark > 35);"));
    }
    
    @Test
    public void exampleTest_18() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +"65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +"55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +"35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +"20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +"submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +"name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +"'Dave';").contains("[OK]"));
        assertEquals("[OK]\n" +
                        "id    name             mark             pass             \n" +
                        "1     'Steve'          65               TRUE             \n" +
                        "4     'Clive'          38               FALSE            \n",
                        sendCommandToServer("SELECT * FROM marks WHERE name LIKE ve;"));
        assertTrue(sendCommandToServer("SELECT * FROM marks WHERE name LIKE 've';").contains("[OK]"));
    }
    
    @Test
    public void exampleTest_19() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +"65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +"55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +"35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +"20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +"submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +"name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +"'Dave';").contains("[OK]"));
        assertEquals("[OK]\n" +
                "id    \n" +
                "3     \n" +
                "4     \n",
                sendCommandToServer("SELECT id FROM " +"marks WHERE pass == FALSE;"));
    }
    
    @Test
    public void exampleTest_20() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +"65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +"55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +"35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +"20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +"submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +"name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +"'Dave';").contains("[OK]"));
        assertEquals("[OK]\n" +
                "name  \n" +
                "'Steve' \n",
                sendCommandToServer("SELECT name FROM marks WHERE "+"mark>60;"));
    }
    
    @Test
    public void exampleTest_21() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +"65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +"55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +"35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +"20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +"submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +"name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +"'Dave';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE mark<" +"40;").contains("[OK]"));
    }
    
    @Test
    public void exampleTest_22() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +"65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +"55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +"35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +"20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +"submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +"name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +"'Dave';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE mark<" +"40;").contains("[OK]"));
        assertEquals("[OK]\n" +
                        "id    name             mark             pass             \n" +
                        "1     'Steve'          65               TRUE             \n" +
                        "                                                         \n" +
                        "                                                         \n" +
                        "                                                         \n",
                        sendCommandToServer("SELECT * FROM marks;"));
    }
    
    @Test
    public void exampleTest_23() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +"65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +"55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +"35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +"20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +"submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +"name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +"'Dave';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE mark<" +"40;").contains("[OK]"));
        assertTrue(sendCommandToServer("SELECT * FROM marks").contains("[ERROR]"));
    }
    
    @Test
    public void exampleTest_24() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +"65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +"55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +"35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +"20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +"submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +"name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +"'Dave';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE mark<" +"40;").contains("[OK]"));
        assertTrue(sendCommandToServer("SELECT * FROM crew;").contains("[ERROR]"));
    }
    
    @Test
    public void exampleTest_25() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +"65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +"55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +"35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +"20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +"submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +"('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +"name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +"'Dave';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE mark<" +"40;").contains("[OK]"));
        String output = sendCommandToServer("SELECT * FROM marks pass == TRUE;");
        assertTrue(sendCommandToServer("SELECT * FROM marks pass == TRUE;").contains("[ERROR]"));
    }
}
