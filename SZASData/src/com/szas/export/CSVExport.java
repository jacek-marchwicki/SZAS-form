package com.szas.export;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;

import com.szas.data.FilledQuestionnaireTuple;

public class CSVExport extends CSVMapper {
	/**
	 * Method to create CSV File from Collection of FilledQuestionnaireTuple and path
	 * @param path path to directory where save csv
	 * @see #exportCSV(java.io.Writer, Collection);
	 */
	public void exportCSVToFile(String path, Collection<FilledQuestionnaireTuple> filledQuestionnaireTuples) throws IOException{
		FileWriter writer = new FileWriter(path);
		exportCSV(writer, filledQuestionnaireTuples);
	}
	
	/**
	 * Import csv from filename
	 * @see #importCSV(Reader, FilledQuestionnaireTuple)
	 */
	public void importCSVFromFile(String filename, FilledQuestionnaireTuple filledQuestionnaireTuple) throws IOException, WrongCSVFile {
		
		//TODO IMPORT ALL FILES FROM CSV
		FileReader fileReader = new FileReader(filename);
		importCSV(fileReader, filledQuestionnaireTuple);
	}
}
