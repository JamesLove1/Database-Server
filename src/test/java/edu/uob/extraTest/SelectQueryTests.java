package edu.uob.extraTest;

import edu.uob.DBServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class SelectQueryTests {
    DBServer server;
    
    @BeforeEach
    public void setup() {
        // Set up the server and clear out the database directory
        server = new DBServer();
//        try {
//            server.dropAll();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    
        sendCommandToServer("CREATE DATABASE testdatabase;");
        sendCommandToServer("USE testdatabase;");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass, " +"worrisomedata);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE, 17.4);");
        sendCommandToServer("INSERT INTO marks VALUES ('Dave', 55, TRUE, " +"fAlse);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 35, FALSE, " +"'willow');");
        sendCommandToServer("INSERT INTO marks VALUES ('Clive', 20, FALSE, " +"40);");
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
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
            "Server took too long to respond (probably stuck in an infinite loop)");
    }
    
    @Test
    public void testInvalidNestedConditions_emptyBrackets() {
        assertTrue(sendCommandToServer("SELECT mark FROM marks WHERE ();").contains("[ERROR]"));
    }
    
    @Test
    public void testInvalidNestedConditions_openParentheses() {
        assertTrue(sendCommandToServer("SELECT mark FROM marks WHERE (pass " +"== true;").contains("[ERROR]"));
    }
    
    @Test
    public void testInvalidNestedConditions_overClosedParentheses() {
        assertTrue(sendCommandToServer("SELECT mark FROM marks WHERE (pass " +"== true));").contains("[ERROR]"));
    }
    
    @Test
    public void testValidNestedConditions_1() {
        assertTrue(sendCommandToServer("SELECT mark FROM marks WHERE (pass ==" +" true);").contains("[OK]"));
    }
    @Test
    public void testValidNestedConditions_2() {
        assertEquals("[OK]\n" +
            "mark\t\n" +
            "65\t\n" +
            "55\t\n", sendCommandToServer("SELECT mark FROM marks WHERE (pass ==" +
            " true);"));
    }
    
    
    @Test
    public void testValidNestedConditions_3() {
        assertTrue(sendCommandToServer("SELECT name FROM marks WHERE " +
            "(WorrisOMEdaTa == false);").contains("[OK]"));
    }
    @Test
    public void testValidNestedConditions_4() {
        assertEquals("[OK]\n" +
            "name\t\n" +
            "Dave\t\n", sendCommandToServer("SELECT name FROM marks WHERE " +
            "(WorrisOMEdaTa == false);"));
    }
    
    
    
    @Test
    public void testValidNestedConditions_5() {
        assertTrue(sendCommandToServer("SELECT name FROM marks WHERE " +
            "(WorrisOMEdaTa > 3);").contains("[OK]"));
    }
    @Test
    public void testValidNestedConditions_6() {
        assertEquals("[OK]\n" +
            "name\t\n" +
            "Clive\t\n", sendCommandToServer("SELECT name FROM marks WHERE " +
            "(WorrisOMEdaTa > 30);"));
    }
    
    @Test
    public void testValidNestedConditions_7() {
        assertTrue(sendCommandToServer("SELECT name FROM marks WHERE " +
            "(WorrisOMEdaTa < 30);").contains("[OK]"));
    }
    @Test
    public void testValidNestedConditions_8() {
        assertEquals("[OK]\n" +
            "name\t\n" +
            "Steve\t\n", sendCommandToServer("SELECT name FROM marks WHERE " +
            "(WorrisOMEdaTa < 30);"));
    }
    
    @Test
    public void testValidNestedConditions_9() {
        assertTrue(sendCommandToServer("SELECT mark, name FROM marks WHERE " +
            "(WorrisOMEdaTa < 30);").contains("[OK]"));
    }
    @Test
    public void testValidNestedConditions_10() {
        assertEquals("[OK]\n" +
            "mark\tname\t\n" +
            "65\tSteve\t\n", sendCommandToServer("SELECT mark, name FROM marks " +
            "WHERE (WorrisOMEdaTa < 30);"));
    }
}
