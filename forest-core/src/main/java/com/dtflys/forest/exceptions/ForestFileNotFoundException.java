package com.dtflys.forest.exceptions;

public class ForestFileNotFoundException extends ForestRuntimeException {


    public ForestFileNotFoundException(String filePath) {
        super("File \"" + filePath + "\" does not exist");
//        this.filePath = filePath;
    }

}
