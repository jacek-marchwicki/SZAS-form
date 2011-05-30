/**
 * 
 */
package com.szas.export;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.supercsv.io.CsvMapReader;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import com.szas.data.FieldIntegerBoxDataTuple;
import com.szas.data.FieldIntegerBoxTuple;
import com.szas.data.FieldTextAreaDataTuple;
import com.szas.data.FieldTextAreaTuple;
import com.szas.data.FieldTextBoxDataTuple;
import com.szas.data.FieldTextBoxTuple;
import com.szas.data.FieldTuple;
import com.szas.data.FilledQuestionnaireTuple;

/**
 * @author pszafer@gmail.com
 *
 */
public class CSVExport {
	
	/**
	 * @param path path to directory where save csv
	 * @param filledQuestionnaireTuples filledQuestionnaireTuples filled Questionnaire tuples, be aware to pass only same category tuples
	 * @throws IOException if file doesn't exist
	 */
	public void exportCSVToFile(String path, Collection<FilledQuestionnaireTuple> filledQuestionnaireTuples) throws IOException{
		ICsvMapWriter writer = new CsvMapWriter(new FileWriter(path), CsvPreference.EXCEL_PREFERENCE);
		try{
			List<Map<String, ? super Object>> listOfAllDate = new ArrayList<Map<String,? super Object>>();
			int i=1;
			String[] headers = null;
			for(FilledQuestionnaireTuple filledQuestionnaireTuple : filledQuestionnaireTuples){
				ArrayList<FieldTuple> filledFields = filledQuestionnaireTuple.getFilledFields();
				Map<String, ? super Object> data = new HashMap<String, Object>();
				if(i == 1){
					 writer.writeHeader(filledQuestionnaireTuple.getName());
					 headers = new String[filledFields.size()+1];
					 headers[0] = "id";
					 ++i;
				}
				for(FieldTuple fieldTuple: filledFields){
					String key = fieldTuple.getName().toString();
					if(i<=headers.length){
						headers[i-1] = key;
						++i;
					}
					data.put("id", fieldTuple.getId());
					if(fieldTuple instanceof FieldTextBoxTuple || fieldTuple instanceof FieldTextBoxDataTuple){
						String value = "";
						value = ((FieldTextBoxTuple)fieldTuple).getValue();
						data.put(key, value);
						
					}
					else if (fieldTuple instanceof FieldTextAreaTuple || fieldTuple instanceof FieldTextAreaDataTuple){
						String value = "";
						value = ((FieldTextAreaTuple)fieldTuple).getValue();
						data.put(key, value);
					}
					else if (fieldTuple instanceof FieldIntegerBoxTuple || fieldTuple instanceof FieldIntegerBoxDataTuple){
						String value = "";
						value = String.valueOf(((FieldIntegerBoxTuple)fieldTuple).getValue());
						data.put(key, value);
					}
				}
				listOfAllDate.add(data);
				if((filledFields.size()-1) == i){
					++i;
				}
			}
			writer.writeHeader(headers);
			for(Map<String, ? super Object> data : listOfAllDate){
				writer.write(data, headers);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally
		{
			writer.close();
		}
		
	}
	
	/**
	 * Method to create CSV from Collection of FilledQuestionnaireTuple
	 * @param filledQuestionnaireTuples filled Questionnaire tuples, be aware to pass only same category tuples
	 * @return return created string csv
	 * @throws IOException if writer cannot write
	 */
	public String exportCSVToString(Collection<FilledQuestionnaireTuple> filledQuestionnaireTuples) throws IOException {
		Writer stringWriter = new StringWriter();
		ICsvMapWriter writer = new CsvMapWriter(stringWriter, CsvPreference.EXCEL_PREFERENCE);
		try{
			List<Map<String, ? super Object>> listOfAllDate = new ArrayList<Map<String,? super Object>>();
			int i=1;
			String[] headers = null;
			for(FilledQuestionnaireTuple filledQuestionnaireTuple : filledQuestionnaireTuples){
				ArrayList<FieldTuple> filledFields = filledQuestionnaireTuple.getFilledFields();
				Map<String, ? super Object> data = new HashMap<String, Object>();
				if(i == 1){
					 writer.writeHeader(filledQuestionnaireTuple.getName());
					 headers = new String[filledFields.size()+1];
					 headers[0] = "id";
					 ++i;
				}
				for(FieldTuple fieldTuple: filledFields){
					String key = fieldTuple.getName().toString();
					if(i<=headers.length){
						headers[i-1] = key;
						++i;
					}
					data.put("id", fieldTuple.getId());
					if(fieldTuple instanceof FieldTextBoxTuple || fieldTuple instanceof FieldTextBoxDataTuple){
						String value = "";
						value = ((FieldTextBoxTuple)fieldTuple).getValue();
						data.put(key, value);
						
					}
					else if (fieldTuple instanceof FieldTextAreaTuple || fieldTuple instanceof FieldTextAreaDataTuple){
						String value = "";
						value = ((FieldTextAreaTuple)fieldTuple).getValue();
						data.put(key, value);
					}
					else if (fieldTuple instanceof FieldIntegerBoxTuple || fieldTuple instanceof FieldIntegerBoxDataTuple){
						String value = "";
						value = String.valueOf(((FieldIntegerBoxTuple)fieldTuple).getValue());
						data.put(key, value);
					}
				}
				listOfAllDate.add(data);
				if((filledFields.size()-1) == i){
					++i;
				}
			}
			writer.writeHeader(headers);
			for(Map<String, ? super Object> data : listOfAllDate){
				writer.write(data, headers);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally
		{
			writer.close();
		}
		return stringWriter.toString();
	}
	
	/**
	 * Import FilledQuestionnaireTuple from CSV file
	 * CSV file structure:
	 * 1st header is name of questionnaire like "Ankieta dentystyczna"
	 * 2nd headers are columns of FilledQuestionnaireTuple to input like "id,Imię,Nazwisko,Który ząb,Opis bólu"
	 * Rest of file, are data to input.
	 * If there are null entries it should be skipped
	 * Standard separator is comma, CSV file accepts space in names
	 * 
	 * @param filename name of file to read
	 * @param filledQuestionnaireTuple questionnaireTuple to input to get filledFields array
	 * @return filled FilledQuestionnaireTuple
	 * @throws IOException if file doesn't exist or if header doesn't exists
	 */
	public FilledQuestionnaireTuple importCSVFromFile(String filename, FilledQuestionnaireTuple filledQuestionnaireTuple) throws IOException{
		FileReader fileReader = new FileReader(filename);
		ICsvMapReader reader = new CsvMapReader(fileReader, CsvPreference.EXCEL_PREFERENCE);
		try{
		String[] headers0 = reader.getCSVHeader(false);
		String[] headers1 = reader.getCSVHeader(false);
		filledQuestionnaireTuple.setName(headers0[0]);
		Map<String, String> mapa = null;
		ArrayList<FieldTuple> filledFields = filledQuestionnaireTuple.getFilledFields();
		while((mapa = reader.read(headers1)) != null){
		for(FieldTuple fieldTuple : filledFields){
			String key = fieldTuple.getName().toString();
			String value = mapa.get(key);
			if(fieldTuple instanceof FieldTextBoxTuple || fieldTuple instanceof FieldTextBoxDataTuple){
				((FieldTextBoxTuple)fieldTuple).setValue(value);
			}
			else if (fieldTuple instanceof FieldTextAreaTuple || fieldTuple instanceof FieldTextAreaDataTuple){
				((FieldTextAreaTuple)fieldTuple).setValue(value);
			}
			else if (fieldTuple instanceof FieldIntegerBoxTuple || fieldTuple instanceof FieldIntegerBoxDataTuple){
				((FieldIntegerBoxTuple)fieldTuple).setValue(Integer.parseInt(value));				
			}
		}
		}
		filledQuestionnaireTuple.setFilledFields(filledFields);
		}
		finally{
			reader.close();
		}
		return filledQuestionnaireTuple;
	}
	
	/**
	 * Import FilledQuestionnaireTuple from CSV file
	 * CSV file structure:
	 * 1st header is name of questionnaire like "Ankieta dentystyczna"
	 * 2nd headers are columns of FilledQuestionnaireTuple to input like "id,Imię,Nazwisko,Który ząb,Opis bólu"
	 * Rest of file, are data to input.
	 * If there are null entries it should be skipped
	 * Standard separator is comma, CSV file accepts space in names
	 * @param csv string with csv in it, it should work for both line separators - \n and \r\n
	 * @param filledQuestionnaireTuple questionnaireTuple to input to get filledFields array
	 * @return filled FilledQuestionnaireTuple
	 * @throws IOException if file doesn't exist or if header doesn't exists
	 */
	public FilledQuestionnaireTuple importCSVFromString(String csv, FilledQuestionnaireTuple filledQuestionnaireTuple) throws IOException{
		Reader stringReader = new StringReader(csv);
		ICsvMapReader reader = new CsvMapReader(stringReader, CsvPreference.EXCEL_PREFERENCE);
		try{
		String[] headers0 = reader.getCSVHeader(false);
		String[] headers1 = reader.getCSVHeader(false);
		filledQuestionnaireTuple.setName(headers0[0]);
		Map<String, String> mapa = null;
		ArrayList<FieldTuple> filledFields = filledQuestionnaireTuple.getFilledFields();
		while((mapa = reader.read(headers1)) != null){
		for(FieldTuple fieldTuple : filledFields){
			String key = fieldTuple.getName().toString();
			String value = mapa.get(key);
			if(fieldTuple instanceof FieldTextBoxTuple || fieldTuple instanceof FieldTextBoxDataTuple){
				((FieldTextBoxTuple)fieldTuple).setValue(value);
			}
			else if (fieldTuple instanceof FieldTextAreaTuple || fieldTuple instanceof FieldTextAreaDataTuple){
				((FieldTextAreaTuple)fieldTuple).setValue(value);
			}
			else if (fieldTuple instanceof FieldIntegerBoxTuple || fieldTuple instanceof FieldIntegerBoxDataTuple){
				((FieldIntegerBoxTuple)fieldTuple).setValue(Integer.parseInt(value));				
			}
		}
		}
		filledQuestionnaireTuple.setFilledFields(filledFields);
		}
		finally{
			reader.close();
		}
		return filledQuestionnaireTuple;
	}
	
}
