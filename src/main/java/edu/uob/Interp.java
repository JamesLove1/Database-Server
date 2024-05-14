package edu.uob;

//import java.lang.foreign.StructLayout;

//import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.util.ArrayList;
import java.util.Objects;

public class Interp {
    private ArrayList<String> tokens = new ArrayList<>();

    private String storageFolderPath;

    private int conditionalIndex;

    private int numBrackets = 0;

    public Interp() {
    }

    public Interp(ArrayList<String> tokens, String storageFolderPath) {
        this.tokens = tokens;
        this.storageFolderPath = storageFolderPath;
    }

    public ArrayList<String> getTokens() {
        return tokens;
    }

    //need to work on this bit latter on in the process
    public String interpTokens() throws IOException, Errors {
        if (!validCommand(this.tokens)) {
            return "[ERROR] - In Valid Command include ';'";
        }
        return commandType(this.tokens);
    }

    private boolean validCommand(ArrayList<String> tokens) {
        boolean isValidCommand = false;
        for (CommandType commandType : CommandType.values()) {
            if (commandType.name().equalsIgnoreCase(tokens.get(0))) {
                isValidCommand = true;
                break;
            }
        }
        int len = tokens.size() - 1;
        if (!Objects.equals(tokens.get(len), ";")) {
            isValidCommand = false;
        }
        return isValidCommand;
    }

    private String commandType(ArrayList<String> tokens) throws IOException, Errors {
        CommandType tokenSwitch = CommandType.valueOf(tokens.get(0).toUpperCase());
        return switch (tokenSwitch) {
            case USE -> this.use(tokens);
            case CREATE -> this.create(tokens);
            case DROP -> this.drop(tokens);
            case ALTER -> this.alter(tokens);
            case INSERT -> this.insert(tokens);
            case SELECT -> this.select(tokens);
            case UPDATE -> this.update(tokens);
            case DELETE -> this.delete(tokens);
            case JOIN -> this.join(tokens);
            default -> "[ERROR] - switch";
        };
    }

    private String use(ArrayList<String> tokens) {
        if (!checkPlainText(tokens.get(1))) {
            return "[ERROR] - Not Plaintext";
        }
        Database db = new Database(this.storageFolderPath);
        return db.setDBName(tokens.get(1));
    }

    private String create(ArrayList<String> tokens) throws IOException, Errors {
        if (tokens.get(1).equalsIgnoreCase("database")) { return createDatabase(tokens); }
        if (tokens.get(1).equalsIgnoreCase("TABLE")) { return createTable(tokens); }
        return "[ERROR] - Create";
    }

    private String createDatabase(ArrayList<String> tokens) {
        if (!checkPlainText(tokens.get(2))) { return "[ERROR] - Not Plaintext";}
        if(reservedWords(tokens.get(2))){ return "[ERROR] - Not Plaintext";}
        Database db = new Database(this.storageFolderPath);
        return db.createDB(tokens.get(2));
    }

    private String createTable(ArrayList<String> tokens) throws IOException, Errors {
        if (!checkPlainText(tokens.get(2))) { return "[ERROR] - Not Plaintext";}
        if(reservedWords(tokens.get(2))){ throw new Errors("reserved key word present");}
        if(tokens.get(3).equalsIgnoreCase("(")){
            if(!tokens.get(tokens.size()-2).equalsIgnoreCase(")")){ throw new Errors("no closing )");}
//            throw new Errors("no opening )");
        }
        Table newTable = new Table(this.storageFolderPath, this.tokens.get(2));
        return newTable.createTable(tokens);
    }

    private String drop(ArrayList<String> tokens) {
        if (tokens.get(1).equalsIgnoreCase("database")) {
            return this.dropDatabase(tokens);
        }
        if (tokens.get(1).equalsIgnoreCase("TABLE")) {
            return this.dropTable(tokens); //drop table edit this && test
        }
        return "[ERROR] - Drop";
    }

    private String dropDatabase(ArrayList<String> tokens) {
        if (!checkPlainText(tokens.get(2))) {
            return "[ERROR] - Not Plaintext";
        }
        Database db = new Database(this.storageFolderPath);
        return db.dropDB(tokens.get(2));
    }

    private String dropTable(ArrayList<String> tokens) {
        if (!checkPlainText(tokens.get(2))) {
            return "[ERROR] - Not Plaintext";
        }
        Table table = new Table(this.storageFolderPath, tokens.get(2));
        return table.dropTable();
    }

    private String alter(ArrayList<String> tokens) throws IOException, Errors {
        if (!tokens.get(1).equalsIgnoreCase("TABLE")) {
            return "[ERROR] - With Syntax";
        }
        if (!checkPlainText(tokens.get(2))) {
            return "[ERROR] - Not Plaintext";
        }
        boolean add = tokens.get(3).equalsIgnoreCase("ADD");
        boolean drop = tokens.get(3).equalsIgnoreCase("DROP");
        if (!add && !drop) {
            return "[ERROR] - With Syntax";
        }
        if (!checkPlainText(tokens.get(4))) {
            return "[ERROR] - Not Plaintext";
        }
        if(reservedWords(tokens.get(4))){ throw new Errors("reserved Key word"); }
        if(tokens.get(4).equalsIgnoreCase("ID")){ throw new Errors("can not delete ID Column");}
        Table alterTable = new Table(this.storageFolderPath, this.tokens.get(2));
        return alterTable.alter(tokens);
    }

    private String insert(ArrayList<String> tokens) throws IOException {
        if (!tokens.get(1).equalsIgnoreCase("INTO")) {
            return "[ERROR] - With Syntax";
        }
        if (!checkPlainText(tokens.get(2))) {
            return "[ERROR] - Not Plaintext";
        }
        if (!tokens.get(3).equalsIgnoreCase("VALUES")) {
            return "[ERROR] - With Syntax";
        }
        if (!tokens.get(4).equalsIgnoreCase("(")) {
            return "[ERROR] - With Syntax";
        }
        if (!tokens.get(tokens.size() - 2).equalsIgnoreCase(")")) {
            return "[ERROR] - With Syntax";
        }
        for (int i = 4; i < tokens.size() - 2; i++) {
            Boolean stringSntax = !this.checkStringLiteral(tokens.get(i));
            Boolean boolSntax = !this.checkBooleanLiteral(tokens.get(i));
            Boolean floatSntax = !this.checkFloatLiteral(tokens.get(i));
            Boolean intSntax = !this.checkIntegerLiteral(tokens.get(i));
            Boolean nullSntax = !this.checkNULL(tokens.get(i));
            if (stringSntax && boolSntax && floatSntax && intSntax && nullSntax) {
                return "[ERROR] - ValueList";
            }
        }
        Table insertIntoTable = new Table(this.storageFolderPath, this.tokens.get(2));
        return insertIntoTable.insert(tokens);
    }

    private String select(ArrayList<String> tokens) throws FileNotFoundException, Errors {
        int fromIndex = 1;
        for (int i = 1; i < tokens.size(); i++) {
            if (tokens.get(i).equalsIgnoreCase("from")) {
                fromIndex = i;
            }
        }
        if (fromIndex < 1) {
            return "[ERROR] - Issue with FROM";
        }
        if (tokens.get(fromIndex - 1).equalsIgnoreCase(",")) {
            return "[ERROR] - incorrect syntax";
        }
        if (!tokens.get(1).equalsIgnoreCase("*")) {
            if (!attributeList(tokens, 1, fromIndex)) {
                return "[ERROR]";
            }
        }
        Table selectTable = new Table(this.storageFolderPath, this.tokens.get(fromIndex + 1));
        if(wherePresent(tokens)){
            if(!where(tokens)){ return "[ERROR]" ;}
            if(!whereGraterThanFrom(tokens)){ return "[ERROR] "; }
        } else {
            if(!tokens.get(tokens.size()-1).equalsIgnoreCase(";")){ return "[ERROR]";}
        }
        return selectTable.select(tokens);
    }

    private String update(ArrayList<String> tokens) throws FileNotFoundException, Errors {

        if (!checkPlainText(tokens.get(1))) { return "[ERROR] - Not Plaintext"; }

        if (!tokens.get(2).equalsIgnoreCase("SET")) { return "[ERROR] - With Syntax"; }

        if(!nameValueList(tokens,3)){ return "[ERROR] - with Name Value List" ; }

        if(!whereGraterThanSet(tokens)){ return "[ERROR]";}

        if(wherePresent(tokens)){
            if(!where(tokens)){ return "[ERROR] - with WHERE statment" ;}
        }
        Table updateTable = new Table(this.storageFolderPath, this.tokens.get(1));
        return updateTable.update(tokens);
    }

    private String delete(ArrayList<String> tokens) throws FileNotFoundException, Errors {

        if (!tokens.get(1).equalsIgnoreCase("FROM")) { return "[ERROR] - With Syntax"; }

        if (!checkPlainText(tokens.get(2))) { return "[ERROR] - Not Plaintext"; }

        if (!tokens.get(3).equalsIgnoreCase("WHERE")) { return "[ERROR] - With Syntax"; }

        if(wherePresent(tokens)){
            if(!where(tokens)){ return "[ERROR] - with WHERE statment" ;}
        }
        Table deleteTable = new Table(this.storageFolderPath, this.tokens.get(2));
        return deleteTable.delete(tokens);
    }

    private String join(ArrayList<String> tokens) throws FileNotFoundException {

        if (!checkPlainText(tokens.get(1))) { return "[ERROR] - Not Plaintext"; }

        if (!tokens.get(2).equalsIgnoreCase("AND")) { return "[ERROR] - With Syntax"; }

        if (!checkPlainText(tokens.get(3))) { return "[ERROR] - Not Plaintext"; }

        if (!tokens.get(4).equalsIgnoreCase("ON")) { return "[ERROR] - With Syntax"; }

        if (!checkPlainText(tokens.get(5))) { return "[ERROR] - Not Plaintext"; }

        if (!tokens.get(6).equalsIgnoreCase("AND")) { return "[ERROR] - With Syntax"; }

        if (!checkPlainText(tokens.get(7))) { return "[ERROR] - Not Plaintext"; }

        Table joinTable = new Table(this.storageFolderPath, this.tokens.get(2));
        return joinTable.join(tokens);
    }

    private boolean nameValueList(ArrayList<String> tokens, int index){

        if(!checkPlainText(tokens.get(index))){ return false;}
        if(tokens.get(index).equalsIgnoreCase("id")){ return false; }
        index++;
        if (!tokens.get(index).equalsIgnoreCase("=")){ return false;}
        index++;
        if(!value(tokens, index)){ return false ;}
        index++;
        
        if(tokens.get(index).equalsIgnoreCase("where")){
            return true;
        } else if (tokens.get(index).equalsIgnoreCase(",")) {
            index++;
            if(nameValueList(tokens,index)){
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    private boolean whereGraterThanSet(ArrayList<String> tokens){
        int setIndex = 1;
        for (int i = 1; i < tokens.size(); i++) {
            if (tokens.get(i).equalsIgnoreCase("set")) {
                setIndex = i;
            }
        }
        int whereIndex = 1;
        for (int i = 1; i < tokens.size(); i++) {
            if (tokens.get(i).equalsIgnoreCase("where")) {
                whereIndex = i;
            }
        }
        if(whereIndex > setIndex){
            return true;
        }
        return false;
    }

    private boolean whereGraterThanFrom(ArrayList<String> tokens){
        int fromIndex = 1;
        for (int i = 1; i < tokens.size(); i++) {
            if (tokens.get(i).equalsIgnoreCase("from")) {
                fromIndex = i;
            }
        }
        int whereIndex = 1;
        for (int i = 1; i < tokens.size(); i++) {
            if (tokens.get(i).equalsIgnoreCase("where")) {
                whereIndex = i;
            }
        }
        if(whereIndex > fromIndex+1){
            return true;
        }
        return false;
    }

    private boolean wherePresent(ArrayList<String> tokens){
        for (int i = 1; i < tokens.size(); i++) {
            if (tokens.get(i).equalsIgnoreCase("where")) {
                return true;
            }
        }
        return false;
    }

    private boolean where(ArrayList<String> tokens) throws Errors {
        int whereIndex = 1;
        for (int i = 1; i < tokens.size(); i++) {
            if (tokens.get(i).equalsIgnoreCase("where")) {
                whereIndex = i;
            }
        }
        if(whereIndex >= 3){
            whereIndex++;
            this.conditionalIndex = whereIndex;
            int originalIndex = this.conditionalIndex;
            if (condition(tokens)){
                if(!tokens.get(this.conditionalIndex).equalsIgnoreCase(";")){return false;}
                return true;
            }
        }
        return false;
    }

    private boolean condition(ArrayList<String> tokens) throws Errors {
        int originalIndex = conditionalIndex;

        if(bracketOpen(tokens)){
            conditionalIndex++;
            this.numBrackets++;
        }

        if(!atributeNameAndValue(tokens)){ return false; }

        if(tokens.get(conditionalIndex).equalsIgnoreCase(";")){
            if(this.numBrackets > 0){return false;}
            return true;
        } else if (booleanOperator(tokens)) {
            conditionalIndex++;
            boolean result = condition(tokens);
            if(bracketClosed(tokens)){
                conditionalIndex++;
                this.numBrackets--;
            }
            if(this.numBrackets != 0){return false;}
            return result;
        }
        return true;
    }

    private boolean atributeNameAndValue(ArrayList<String> tokens) throws Errors {
        int originalIndex = conditionalIndex;
        if(bracketOpen(tokens)){
            this.numBrackets++;
            conditionalIndex++;
        }

        if(validAttributeName(tokens)){
            conditionalIndex++;
        } else {
            return false;
        }

        if(comparator(tokens)){
            conditionalIndex++;
        } else {
            return false;
        }

        if(value(tokens, conditionalIndex)){
            conditionalIndex++;
        } else {
            return false;
        }

        if(bracketClosed(tokens)){
            this.numBrackets--;
            conditionalIndex++;
        }

        return true;
    }

    private boolean value( ArrayList<String> tokens, int index){
        Boolean stringSntax = !this.checkStringLiteral(tokens.get(index));
        Boolean boolSntax = !this.checkBooleanLiteral(tokens.get(index));
        Boolean floatSntax = !this.checkFloatLiteral(tokens.get(index));
        Boolean intSntax = !this.checkIntegerLiteral(tokens.get(index));
        Boolean nullSntax = !this.checkNULL(tokens.get(index));
        if (stringSntax || boolSntax || floatSntax || intSntax || nullSntax) {
            return true;
        }
        return false;
    }

    private boolean comparator( ArrayList<String> tokens){
        if (tokens.get(conditionalIndex).equals("==")){ return true;}
        if (tokens.get(conditionalIndex).equals(">")){ return true;}
        if (tokens.get(conditionalIndex).equals("<")){ return true;}
        if (tokens.get(conditionalIndex).equals(">=")){ return true;}
        if (tokens.get(conditionalIndex).equals("<=")){ return true;}
        if (tokens.get(conditionalIndex).equals("!=")){ return true;}
        if (tokens.get(conditionalIndex).equalsIgnoreCase("LIKE")){return true;}
        return false;
    }

    private boolean validAttributeName( ArrayList<String> tokens) throws Errors {
        if (checkPlainText(tokens.get(conditionalIndex))) {
            if(reservedWords(tokens.get(conditionalIndex))){ throw new Errors("dont use reserved key word") ; }
            return true;
        }
        return false;
    }

    private boolean bracketOpen( ArrayList<String> tokens){
        if(tokens.get(conditionalIndex).equalsIgnoreCase("(")){
            return true;
        }
        return false;
    }

    private boolean bracketClosed(ArrayList<String> tokens){
        if(tokens.get(conditionalIndex).equalsIgnoreCase(")")){
            return true;
        }
        return false;
    }

    private boolean booleanOperator(ArrayList<String> tokens){
        if(conditionalIndex >= tokens.size()-1){return false;}
        if(tokens.get(conditionalIndex).equals("AND")){
            return true;
        }
        if(tokens.get(conditionalIndex).equals("OR")){
            return true;
        }
        return false;
    }

    private boolean attributeList(ArrayList<String> tokens, int startIndex, int endIndex) {
        boolean attributeName = true;
        for (int i = startIndex; i < endIndex; i++) {
            if (attributeName) {
                if (!checkPlainText(tokens.get(i))) {
                    return false;
                }
                attributeName = false;
            } else if (!attributeName) {
                if (!tokens.get(i).equalsIgnoreCase(",")) {
                    return false;
                }
                attributeName = true;
            }
        }
        return true;
    }

    private boolean reservedWords(String token){
        if(token.equalsIgnoreCase("like")){ return true;}
        if(token.equalsIgnoreCase("false")){ return true;}
        if(token.equalsIgnoreCase("true")){ return true;}
        if(token.equalsIgnoreCase("or")){ return true;}
        if(token.equalsIgnoreCase("and")){ return true;}
        if(token.equalsIgnoreCase("drop")){ return true;}
        if(token.equalsIgnoreCase("database")){ return true;}
        if(token.equalsIgnoreCase("table")){ return true;}
        if(token.equalsIgnoreCase("from")){ return true;}
        if(token.equalsIgnoreCase("set")){ return true;}
        if(token.equalsIgnoreCase("values")){ return true;}
        if(token.equalsIgnoreCase("null")){ return true;}
        return false;
    }

    private boolean checkPlainText(String checkText) {
        return checkText.matches("^[A-Za-z0-9]+$");
    }

    private boolean checkFloatLiteral(String checkText) {
        return checkText.matches("[-+]?\\d+(\\.\\d+)?");
    }

    private boolean checkIntegerLiteral(String checkText) {
        return checkText.matches("^[+-]?[0-9]+$");
    }

    private boolean checkStringLiteral(String checkText) {
        return checkText.matches("['\"]?[ A-Za-z0-9!#\\$%&\\(\\)\\*\\+,\\-./:;<=>\\?@\\[\\]\\\\^_`{|}~]*['\"]?");
    }

    private boolean checkBooleanLiteral(String checkText) {
        return checkText.matches("TRUE|FALSE");
    }

    private boolean checkNULL(String checkText) {
        return checkText.matches("NULL");
    }

}
