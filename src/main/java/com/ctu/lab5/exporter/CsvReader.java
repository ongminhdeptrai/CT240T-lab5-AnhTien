package com.ctu.lab5.exporter;

/** Concrete Product mới: bộ đọc file CSV. */
class CsvReader implements DocumentReader {
    @Override
    public String readContent(String filePath) {
        return "Content from CSV file: " + filePath;
    }
}

