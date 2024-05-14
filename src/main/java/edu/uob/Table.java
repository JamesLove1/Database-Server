package edu.uob;

//import java.awt.desktop.SystemEventListener;
import java.io.*;
import java.util.*;


public class Table {

    private String storageFolderPath;

    private String DB;

    private String tableName;

    private ArrayList<ArrayList<String>> dataFrame;

    public Table(String storageFolderPath , String tableName){
        this.storageFolderPath = storageFolderPath;
        this.DB = Database.dbname;
        this.tableName = tableName;
        this.dataFrame = new ArrayList<ArrayList<String>>();
    }

    public ArrayList<ArrayList<String>> getDataFrame(){
        return this.dataFrame;
    }

    private Boolean storageFolderPathAndDBExsits () {
        String path = this.storageFolderPath+File.separator+DB;
        File newTable = new File(path);
        return newTable.exists();
    }

    private Boolean tableExists(){
        String path = this.storageFolderPath+File.separator+this.DB+File.separator+this.tableName+".tab";
        File tablePath = new File(path);
        if(tablePath.exists()){
            return true;
        }
        return false;
    }

    public String insert(ArrayList<String> tokens) throws IOException {
        if(!this.tableExists()){return "[ERROR] - Table does not exsist" ;}
        loadTableFile();
        if(!correctNumberValues(tokens)){return "[ERROR] - wrong number of values";}
        writeValueList(tokens);
        return toTable();
    }

    private boolean correctNumberValues(ArrayList<String> tokens){
        int numOfValues = 0;
        for (int i = 4; i < tokens.size() - 3; i++) {
            if(!tokens.get(i).equalsIgnoreCase(",")){
                numOfValues++;
            }
        }
        if(numOfValues == this.dataFrame.get(0).size()-1){return true;}
        return false;
    }

    public String alter(ArrayList<String> tokens) throws IOException, Errors {
        if(!this.tableExists()){return "[ERROR] - Table does not exsist" ;}
        loadTableFile();
        if(tokens.get(3).equalsIgnoreCase("ADD")){
            for( ArrayList<String> record  : this.dataFrame){
                record.add(" ");
            }
            if(this.dataFrame.get(0).contains(tokens.get(4))){ throw new Errors("Duplicate Attribute"); }
            this.dataFrame.get(0).set(this.dataFrame.get(0).size()-1,tokens.get(4));
        } else {
            int indexToRemove = this.dataFrame.get(0).indexOf(tokens.get(4));
            if(indexToRemove == -1){ throw new Errors("Duplicate Attribute"); }
            for( ArrayList<String> record  : this.dataFrame){
                record.remove(indexToRemove);
            }
        }
        return toTable();
    }

    private void writeValueList(ArrayList<String> tokens) {
        if(!Objects.equals(tokens.get(4), "(") && !Objects.equals(tokens.get(tokens.size() - 2), ")")){}

        ArrayList<String> valueList = new ArrayList<>();
        String ID = String.valueOf(this.dataFrame.size());
        valueList.add(ID);
        for(int i = 5; i < (tokens.size()-2); i++){
            if(!Objects.equals(tokens.get(i), ",")){
                valueList.add(tokens.get(i));
            }
        }
        this.dataFrame.add(valueList);

    }

    public String select(ArrayList<String> tokens) throws FileNotFoundException, Errors {
        if(!this.tableExists()){return "[ERROR] - Table does not exsist" ;}
        loadTableFile();

        if(wherePresent(tokens)){
            this.dataFrame = where(tokens);
        }

        if (tokens.get(1).equalsIgnoreCase("*")){ return printTable(); }

        int indexFROM = 1;
        for (int i = 1; i < tokens.size(); i++) {
            if(tokens.get(i).equalsIgnoreCase("FROM")){ indexFROM = i ;}
        }

        ArrayList<Integer> indexCOLUMNStoKeep = new ArrayList<>();
        for (int tokenIndex = 1; tokenIndex < indexFROM; tokenIndex++) {
            if (!tokens.get(tokenIndex).equalsIgnoreCase(",")) {
                if(reservedWords(tokens.get(tokenIndex))){ throw new Errors("dont use reserved Key words");};
//                indexCOLUMNStoKeep.add(this.dataFrame.get(0).indexOf(tokens.get(tokenIndex)));
                indexCOLUMNStoKeep.add(indexOfCaseInsensertive(tokens.get(tokenIndex),this.dataFrame,this.dataFrame.get(0).size()));
            }
        }
        ArrayList<ArrayList<String>> printableDf = new ArrayList<>();
        for( int records = 0 ; records < this.dataFrame.size(); records++){
            ArrayList<String> record = new ArrayList<>();
            for(int i = 0; i < indexCOLUMNStoKeep.size(); i++){
                record.add(this.dataFrame.get(records).get(indexCOLUMNStoKeep.get(i)));
            }
            printableDf.add(record);
        }
        this.dataFrame = printableDf;

        return printTable();
    }


    public String update(ArrayList<String> tokens) throws FileNotFoundException, Errors {

        if(!this.tableExists()){return "[ERROR] - Table does not exsist" ;}
        loadTableFile();

        if (!validAttributes(tokens)){ return "[ERROR]" ; }

        ArrayList<ArrayList<String>> updateDataFrame = new ArrayList<ArrayList<String>>();
        if(wherePresent(tokens)){
                updateDataFrame = updateDataFrame(tokens,where(tokens));
        }

        for(ArrayList<String> record : updateDataFrame){
            this.dataFrame.set( Integer.parseInt(record.get(0)) , record );
        }

        return toTable();
    }

    public String delete(ArrayList<String> tokens) throws FileNotFoundException {

        if(!this.tableExists()){return "[ERROR] - Table does not exsist" ;}
        loadTableFile();

        if( !tokens.get(2).equalsIgnoreCase(this.tableName)){return "[ERROR]";}


        ArrayList<ArrayList<String>> deleteDataFrame = new ArrayList<ArrayList<String>>();
        if(wherePresent(tokens)){
            deleteDataFrame = where(tokens);
        }

        ArrayList<String> newRecord = new ArrayList<>();
        for ( String cell : this.dataFrame.get(0)){
            newRecord.add(" ");
        }

        deleteDataFrame.remove(0);
        for(int i = 0; i < deleteDataFrame.size(); i++){
            this.dataFrame.set(Integer.parseInt(deleteDataFrame.get(i).get(0)), newRecord );
        }

        return toTable();
    }

    public void setLoadTableFile() throws FileNotFoundException {
        loadTableFile();
    }

    public String join(ArrayList<String> tokens) throws FileNotFoundException {

        // table 1 set up
        Table leftTable = new Table(this.storageFolderPath, tokens.get(1));
        if(!leftTable.getTableExsits()){return "[ERROR]";}
        leftTable.setLoadTableFile();

        ArrayList<ArrayList<String>> leftDf = leftTable.getDataFrame();
//        int leftAttribute = leftDf.get(0).indexOf(tokens.get(5)); //issue with case sensitivity
        int leftAttribute = indexOfCaseInsensertive(tokens.get(5), leftDf, leftDf.get(0).size());
        if( leftAttribute== -1 ){return "[ERROR]";}

        //table 2 set up
        Table rightTable = new Table(this.storageFolderPath, tokens.get(3));
        if(!rightTable.getTableExsits()){return "[ERROR]";}
        rightTable.setLoadTableFile();

        ArrayList<ArrayList<String>> rightDf = rightTable.getDataFrame();
//        int rightAttribute = rightDf.get(0).indexOf(tokens.get(7));
        int rightAttribute = indexOfCaseInsensertive(tokens.get(7), rightDf, rightDf.get(0).size());
        if( rightAttribute == -1 ){return "[ERROR]";}

        this.dataFrame = matchRightAndLeft(tokens.get(1),tokens.get(3), leftDf ,rightDf,
                                            leftAttribute, rightAttribute );
        return printTable(20);
    }

    private int indexOfCaseInsensertive(String token, ArrayList<ArrayList<String>> df, int maxSize){
        for(int i = 0; i < maxSize; i++){
            if(df.get(0).get(i).equalsIgnoreCase(token)){
                return i;
            }
        }
        return -1;
    }

    private ArrayList<ArrayList<String>> matchRightAndLeft( String tableNameLeft, String tableNameRight,
                                                            ArrayList<ArrayList<String>> leftDf,
                                                            ArrayList<ArrayList<String>> rightDf,
                                                            int leftAttribute, int rightAttribute){
        int ID = 1;
        ArrayList<ArrayList<String>> innerJoin = new ArrayList<ArrayList<String>>();

        innerJoin.add(this.header(tableNameLeft, tableNameRight, leftDf, rightDf));

        for (int rowLeft = 1 ; rowLeft < leftDf.size(); rowLeft++ ){
            for(int rowRight = 1; rowRight < rightDf.size(); rowRight++){

                String leftCell =  leftDf.get(rowLeft).get(leftAttribute);
                String rightCell = rightDf.get(rowRight).get(rightAttribute);

                if( leftCell.equalsIgnoreCase(rightCell)){
                    ArrayList<String> joinedDF = new ArrayList<String>();
                    joinedDF.add(String.valueOf(ID));
                    ID++;

                    ArrayList<String> prepRecordLeft = new ArrayList<String>();
                    prepRecordLeft.addAll(leftDf.get(rowLeft));
                    prepRecordLeft.remove(0);
                    joinedDF.addAll(prepRecordLeft); //leftDf.get(rowLeft)

                    ArrayList<String> prepRecordRight = new ArrayList<String>();
                    prepRecordRight.addAll(rightDf.get(rowRight));
                    prepRecordRight.remove(0); // rightDf.get(rowRight).remove(0);
                    joinedDF.addAll(prepRecordRight); //rightDf.get(rowRight)

                    innerJoin.add(joinedDF);
                }
            }
        }
        return innerJoin;
    }

    private ArrayList<String> header(String tableNameLeft,
                                     String tableNameRight,
                                     ArrayList<ArrayList<String>> leftDf,
                                     ArrayList<ArrayList<String>> rightDf){

        ArrayList<String> headerRecord = new ArrayList<String>();
        headerRecord.add("ID");

        for ( int i = 1; i < leftDf.get(0).size(); i++){
            headerRecord.add( tableNameLeft+"."+leftDf.get(0).get(i));
        }

        for ( int i = 1; i < rightDf.get(0).size(); i++){
            headerRecord.add( tableNameRight+"."+rightDf.get(0).get(i));
        }

        return headerRecord;
    }

    public boolean getTableExsits(){
        return this.tableExists();
    }

    private ArrayList<ArrayList<String>>  updateDataFrame(ArrayList<String> tokens ,
                                 ArrayList<ArrayList<String>> updateDataFrame){

        int indexSET = setIndex(tokens);
        int whereIndex = whereIndex(tokens);
        for(int index = indexSET+1; index < whereIndex; index++ ){

            int columnIndex = updateDataFrame.get(0).indexOf(tokens.get(index));

            index++; index++;
            String value = tokens.get(index);
            for(int record = 1; record < updateDataFrame.size(); record++ ){
                updateDataFrame.get(record).set(columnIndex, value);
            }

        }
        updateDataFrame.remove(0);
        return updateDataFrame;

    }

    private boolean validAttributes(ArrayList<String> tokens) throws Errors {
        int indexSET = setIndex(tokens);
        int whereIndex = whereIndex(tokens);
        for(int index = indexSET+1; index < whereIndex; index++ ){
            if(!this.dataFrame.get(0).contains(tokens.get(index))){ throw new Errors("Attribute names must be unique");}
            int columnIndex = this.dataFrame.get(0).indexOf(tokens.get(index));
            if((columnIndex == -1)){return false;}
            index++; index++;
        }
        return true;
    }

    private int whereIndex(ArrayList<String> tokens){
        int whereIndex = 0;
        for (int i = 1; i < tokens.size(); i++) {
            if (tokens.get(i).equalsIgnoreCase("where")) {
                 return whereIndex = i;
            }
        }
        return -1;
    }

    private int setIndex(ArrayList<String> tokens){
        int indexSET = 0;
        for (int i = 1; i < tokens.size(); i++) {
            if (tokens.get(i).equalsIgnoreCase("set")) {
                return indexSET = i;
            }
        }
        return -1;
    }

    private boolean wherePresent(ArrayList<String> tokens){
        for (int i = 1; i < tokens.size(); i++) {
            if (tokens.get(i).equalsIgnoreCase("where")) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<ArrayList<String>> where (ArrayList<String> tokens){
        ArrayList<ArrayList<String>> quiryDataFrame = new ArrayList<ArrayList<String>>();//= this.dataFrame;

        int whereIndex = 1;
        for (int i = 1; i < tokens.size(); i++) {
            if (tokens.get(i).equalsIgnoreCase("where")) {
                whereIndex = i;
            }
        }

        ArrayList<ArrayList<String>> prevQuiryDataFrame = new ArrayList<>();
        boolean booleanHasPassed = false;
        int booleanHasPassedIndex = 0;

        for (int i = whereIndex+1 ; i < tokens.size(); i++){
            if( !tokens.get(i).equals("(") && !tokens.get(i).equals(")") && !tokens.get(i).equals("OR") &&
                    !tokens.get(i).equals("AND") && !tokens.get(i).equals(";")) {

                prevQuiryDataFrame = quiryDataFrame;
                String column = tokens.get(i);
                String operation = tokens.get(++i);
                String value = tokens.get(++i);
                quiryDataFrame = comparator(column, operation, value);
                if (booleanHasPassed){
                    booleanHasPassed = false;
                    quiryDataFrame = boolValues(prevQuiryDataFrame, quiryDataFrame , tokens.get(booleanHasPassedIndex));
                }
            }
            if( !tokens.get(i).equals("(") && !tokens.get(i).equals(")") && (tokens.get(i).equals("OR") ||
                    tokens.get(i).equals("AND") || tokens.get(i).equals(";"))) {
                booleanHasPassed = true;
                booleanHasPassedIndex = i;
            }
        }
        if(!(quiryDataFrame == null)){
            quiryDataFrame.add(0,this.dataFrame.get(0));
        }
        return quiryDataFrame ;
    }

//    check for issues with reserved key words
    private ArrayList<ArrayList<String>> comparator(String columnName ,String comparator, String value){

        ArrayList<ArrayList<String>> quiryDataFrame = new ArrayList<ArrayList<String>>();
        int indexColumnName = this.dataFrame.get(0).indexOf(columnName);
        if(indexColumnName == -1) { return null; }
        boolean valueCheckFloat = value.matches("^[0-9]*\\.[0-9]+$");
        boolean valueCheckInt = value.matches("^[0-9]+$");
        float convertedValue = -9999999;
        if ((valueCheckFloat || valueCheckInt)) {
            convertedValue = Float.parseFloat(value);
        }

        switch (comparator) {
            case "==" -> { return comparatorEqual(indexColumnName, value, quiryDataFrame, valueCheckFloat,
                    valueCheckInt, convertedValue);}
            case "!=" -> { return comparatorNotEqual(indexColumnName, value, quiryDataFrame, valueCheckFloat,
                    valueCheckInt, convertedValue);}
            case ">" -> { return comparatorGraterThan(indexColumnName, value, quiryDataFrame, valueCheckFloat,
                    valueCheckInt, convertedValue); }
            case "<" -> { return comparatorSmallerThan(indexColumnName, value, quiryDataFrame, valueCheckFloat,
                    valueCheckInt, convertedValue);}
            case ">=" -> { return comparatorGraterThanOrEquals(indexColumnName, value, quiryDataFrame, valueCheckFloat,
                    valueCheckInt, convertedValue);}
            case "<=" -> { return comparatorSmallerThanOrEquals(indexColumnName, value, quiryDataFrame,
                    valueCheckFloat, valueCheckInt, convertedValue); }
            case "LIKE" -> { return comparatorLIKE(indexColumnName, value, quiryDataFrame); }
            default -> { return quiryDataFrame; }
        }
    }

    private ArrayList<ArrayList<String>> comparatorEqual(int indexColumnName, String value,
                                                            ArrayList<ArrayList<String>> quiryDataFrame,
                                                            boolean valueCheckFloat,
                                                            boolean valueCheckInt,
                                                            float convertedValue
    ){
        for (int record = 1; record < this.dataFrame.size(); record++) {
            if (dataFrame.get(record).get(indexColumnName).equalsIgnoreCase(value)) {
                quiryDataFrame.add(this.dataFrame.get(record));
            }
        }
        return quiryDataFrame;
    }

    private ArrayList<ArrayList<String>> comparatorNotEqual(int indexColumnName, String value,
                                                              ArrayList<ArrayList<String>> quiryDataFrame,
                                                              boolean valueCheckFloat,
                                                              boolean valueCheckInt,
                                                              float convertedValue
    ){
        for (int record = 1; record < this.dataFrame.size(); record++) {
            if (!dataFrame.get(record).get(indexColumnName).equalsIgnoreCase(value)) {
                quiryDataFrame.add(this.dataFrame.get(record));
            }
        }
        return quiryDataFrame;
    }

    private ArrayList<ArrayList<String>> comparatorGraterThan(int indexColumnName, String value,
                                                               ArrayList<ArrayList<String>> quiryDataFrame,
                                                               boolean valueCheckFloat,
                                                               boolean valueCheckInt,
                                                               float convertedValue
    ){
        for (int record = 1; record < this.dataFrame.size(); record++) {
            if(indexColumnName == -1 ){return null;}
            boolean attributeCheckFloat = dataFrame.get(record).get(indexColumnName).matches("^[0-9]*\\.[0-9]+$");
            boolean attributeCheckInt = dataFrame.get(record).get(indexColumnName).matches("^[0-9]+$");
            if ((valueCheckFloat || valueCheckInt) && (attributeCheckFloat || attributeCheckInt)) {
                float convertedAttribute = Float.parseFloat(dataFrame.get(record).get(indexColumnName));
                if (convertedAttribute > convertedValue) {
                    quiryDataFrame.add(this.dataFrame.get(record));
                }
            }
        }
        return quiryDataFrame;
    }

    private ArrayList<ArrayList<String>> comparatorSmallerThan(int indexColumnName, String value,
                                                                      ArrayList<ArrayList<String>> quiryDataFrame,
                                                                      boolean valueCheckFloat,
                                                                      boolean valueCheckInt,
                                                                      float convertedValue
    ){
        for (int record = 1; record < this.dataFrame.size(); record++) {
            if(indexColumnName == -1 ){return null;}
            boolean attributeCheckFloat = dataFrame.get(record).get(indexColumnName).matches("^[0-9]*\\.[0-9]+$");
            boolean attributeCheckInt = dataFrame.get(record).get(indexColumnName).matches("^[0-9]+$");
            if((valueCheckFloat || valueCheckInt) && (attributeCheckFloat || attributeCheckInt)){
                float convertedAttribute = Float.parseFloat(dataFrame.get(record).get(indexColumnName));
                if (convertedAttribute < convertedValue) {
                    quiryDataFrame.add(this.dataFrame.get(record));
                }
            }
        }
        return quiryDataFrame;
    }

    private ArrayList<ArrayList<String>> comparatorGraterThanOrEquals(int indexColumnName, String value,
                                                                      ArrayList<ArrayList<String>> quiryDataFrame,
                                                                      boolean valueCheckFloat,
                                                                      boolean valueCheckInt,
                                                                      float convertedValue
                                                                      ){
        for (int record = 1; record < this.dataFrame.size(); record++) {
            if(indexColumnName == -1 ){return null;}
            boolean attributeCheckFloat = dataFrame.get(record).get(indexColumnName).matches("^[0-9]*\\.[0-9]+$");
            boolean attributeCheckInt = dataFrame.get(record).get(indexColumnName).matches("^[0-9]+$");
            if((valueCheckFloat || valueCheckInt) && (attributeCheckFloat || attributeCheckInt)){
                float convertedAttribute = Float.parseFloat(dataFrame.get(record).get(indexColumnName));
                if (convertedAttribute >= convertedValue) {
                    quiryDataFrame.add(this.dataFrame.get(record));
                }
            }
        }
        return quiryDataFrame;
    }

    private ArrayList<ArrayList<String>> comparatorSmallerThanOrEquals(int indexColumnName, String value,
                                                                      ArrayList<ArrayList<String>> quiryDataFrame,
                                                                      boolean valueCheckFloat,
                                                                      boolean valueCheckInt,
                                                                      float convertedValue
    ){
        for (int record = 1; record < this.dataFrame.size(); record++) {
            if(indexColumnName == -1 ){return null;}
            boolean attributeCheckFloat = dataFrame.get(record).get(indexColumnName).matches("^[0-9]*\\.[0-9]+$");
            boolean attributeCheckInt = dataFrame.get(record).get(indexColumnName).matches("^[0-9]+$");
            if((valueCheckFloat || valueCheckInt) && (attributeCheckFloat || attributeCheckInt)){
                float convertedAttribute = Float.parseFloat(dataFrame.get(record).get(indexColumnName));
                if (convertedAttribute <= convertedValue) {
                    quiryDataFrame.add(this.dataFrame.get(record));
                }
            }
        }
        return quiryDataFrame;
    }

    private ArrayList<ArrayList<String>> comparatorLIKE(int indexColumnName, String value,
                                                        ArrayList<ArrayList<String>> quiryDataFrame){
        for (int record = 1; record < this.dataFrame.size(); record++) {
            if (dataFrame.get(record).get(indexColumnName).contains(value)) { //.equals(value)
                quiryDataFrame.add(this.dataFrame.get(record));
            }
        }
        return quiryDataFrame;
    }



    private ArrayList<ArrayList<String>> boolValues(ArrayList<ArrayList<String>> prevQuiryDataFrame,
                                                    ArrayList<ArrayList<String>> quiryDataFrame,
                                                    String token ){

        switch (token){
            case "OR" -> {
                for(ArrayList<String> record : prevQuiryDataFrame){
                    int index = quiryDataFrame.indexOf(record);
                    if(index == -1){
                        quiryDataFrame.add(record);
                    }
                }
                return quiryDataFrame;
            }
            case "AND" -> {
                quiryDataFrame.retainAll(prevQuiryDataFrame);
                return quiryDataFrame;
            }
            case ";" -> {
                System.out.println(token);
                return quiryDataFrame;
            }
        }
        return null;
    }

    private boolean isNumeric(String str) {
        try {
            // Try to parse the string as a float
            Float.parseFloat(str);
            return true; // Parsing succeeded, the string is numeric
        } catch (NumberFormatException e) {
            return false; // Parsing failed, the string is not numeric
        }
    }


    private String printTable() {
        StringBuilder printJoin = new StringBuilder();
        StringBuilder columnFormat = new StringBuilder("%-5s"); // %-15s %-15s %-15s %-15s
        if(this.dataFrame.isEmpty()){ return "[OK]";}
        for( int i = 1; i < this.dataFrame.get(0).size(); i++){
            columnFormat.append(" %-15s ");
        }
        columnFormat.append(" %n");

        for (int i = 0; i < this.dataFrame.size(); i++) {
            List<String> row = this.dataFrame.get(i);
            printJoin.append(String.format(columnFormat.toString(), row.toArray()));
        }

        return "[OK]\n" + printJoin.toString();
    }

    private String printTable(int colemnSize) {

        StringBuilder printJoin = new StringBuilder();
        StringBuilder columnFormat = new StringBuilder("%-5s"); // %-15s %-15s %-15s %-15s
        for( int i = 1; i < this.dataFrame.get(0).size(); i++){
            columnFormat.append(" %-"+String.valueOf(colemnSize)+"s ");
        }
        columnFormat.append(" %n");

        for (int i = 0; i < this.dataFrame.size(); i++) {
            List<String> row = this.dataFrame.get(i);
            printJoin.append(String.format(columnFormat.toString(), row.toArray()));
        }

        return "[OK]\n" + printJoin.toString();
    }


//    edit handling for commans
    public String createTable(ArrayList<String> tokens) throws IOException, Errors {
        String errorMessage = "Database does not exits. Use create and use command.";
        if(!storageFolderPathAndDBExsits()){ return "[ERROR] - "+errorMessage;}
        String path = this.storageFolderPath+File.separator+DB+File.separator+this.tableName+".tab";
        File newTable = new File(path);
        if(!newTable.exists()){
            newTable.createNewFile();
            writeAttributes(newTable,tokens);
            return "[OK]";
        }
        return "[ERROR] - Table already exsits";
    }

    private void writeAttributes(File newTable, ArrayList<String> tokens) throws Errors {
        if(Objects.equals(tokens.get(3), "(") && Objects.equals(tokens.get(tokens.size() - 2), ")")){
            ArrayList<String> attributes = new ArrayList<>();
            attributes.add("id");
            for(int i = 4; i < (tokens.size()-2); i++){
                if(!Objects.equals(tokens.get(i), ",")){
                    if(attributes.contains(tokens.get(i))){ throw new Errors("Duplicate Attribute"); }
                    attributes.add(tokens.get(i)); }
            }
            this.dataFrame.add(0, attributes);
            toTable();
        }
    }

    private String toTable(){
        String path = this.storageFolderPath+File.separator+this.DB+File.separator+this.tableName+".tab";
        File tablePath = new File(path);
        if(!tablePath.exists()){ return "[ERROR]"; }
        StringBuilder finalList = new StringBuilder();
        for(int i = 0; i < this.dataFrame.size() ; i++){
                String prepString = String.join("\t",this.dataFrame.get(i));
                finalList.append(prepString).append("\n");
        }
        try {
            FileWriter writer = new FileWriter(tablePath);
            writer.write(finalList.toString());
            writer.flush();
            writer.close();
        } catch (IOException e){
            return "[ERROR]";
        }
        return "[OK]";
    }

    private String loadTableFile() throws FileNotFoundException {
        String path = this.storageFolderPath+File.separator+this.DB+File.separator+this.tableName+".tab";
        File fileToOpen = new File(path);
        if(!fileToOpen.exists()){ return "[ERROR]"; }
        this.dataFrame = new ArrayList<ArrayList<String>>();
        try {
            FileReader reader = new FileReader(fileToOpen);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null){
                ArrayList<String> lineList = new ArrayList<>();
                String[] parts = line.split("\t");
                for (String part : parts) {
                    lineList.add(part);
                }
                // Add the completed lineList to dataFrame
                this.dataFrame.add(lineList);
            }
            bufferedReader.close();
        } catch (IOException e){
            return "[ERROR]";
        }
        return "[OK]";
    }

    public String dropTable() {
        String path = this.storageFolderPath+File.separator+this.DB+File.separator+this.tableName+".tab";
        File table = new File(path);
        if(table.exists()){
            table.delete();
            return "[OK]";
        }
        return "[ERROR] - table does not exsit";
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
}
