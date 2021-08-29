package olszowka.expenseorganizer.controllers;

import java.io.File;
import java.io.IOException;

public class DataController {
    private File incomesFile;
    private File outcomesFile;
    private File directory;

    public DataController() throws IOException {
        initializeFiles();
    }


    private void initializeFiles() throws IOException {
        this.directory = new File("src/main/resources/data/json");
        if(directory.mkdirs()) {
            System.out.println("Data directory successfully created.");
        } else {
            System.out.println("Data directory already exists.");
        }

        this.incomesFile = new File("src/main/resources/data/json/incomesData.json");

        if(incomesFile.createNewFile()) {
            System.out.println("IncomesFile successfully created.");
        } else {
            if(incomesFile.exists()) {
                System.out.println("File already exists.");
            } else {
                System.err.println("Error during file creation");
            }
        }

        this.outcomesFile = new File("src/main/resources/data/json/outcomesData.json");

        if(outcomesFile.createNewFile()) {
            System.out.println("IncomesFile successfully created.");
        } else {
            if(outcomesFile.exists()) {
                System.out.println("File already exists.");
            } else {
                System.err.println("Error during file creation");
            }
        }
    }


}
