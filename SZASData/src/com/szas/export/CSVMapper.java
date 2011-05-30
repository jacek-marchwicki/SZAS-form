/**
 * 
 */
package com.szas.export;

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
public class CSVMapper {

	/**
	 * Method to create CSV from Collection of FilledQuestionnaireTuple
	 * @param writer writer for saving csv file
	 * @param filledQuestionnaireTuples filledQuestionnaireTuples
	 * filled Questionnaire tuples, be aware to pass only same category tuples
	 * @throws IOException When in IO exception occur
	 */
	public void exportCSV(Writer writer,
			Collection<FilledQuestionnaireTuple> filledQuestionnaireTuples)
	throws IOException {
		ICsvMapWriter csvWriter = new CsvMapWriter(writer, CsvPreference.EXCEL_PREFERENCE);
		try {
			List<Map<String, ? super Object>> listOfAllData =
				new ArrayList<Map<String,? super Object>>();
			String[] headers = null;
			int i=1;
			for(FilledQuestionnaireTuple filledQuestionnaireTuple : filledQuestionnaireTuples){
				ArrayList<FieldTuple> filledFields =
					filledQuestionnaireTuple.getFilledFields();
				Map<String, ? super Object> data = new HashMap<String, Object>();
				if(i == 1){
					csvWriter.writeHeader(filledQuestionnaireTuple.getName());
					headers = new String[filledFields.size()+1];
					headers[0] = "id";
					++i;
				}
				for(FieldTuple fieldTuple: filledFields){
					String key = fieldTuple.getName();
					if(i<=headers.length){
						headers[i-1] = key;
						++i;
					}
					data.put("id", fieldTuple.getId());
					String value = fieldTuple.toString();
					data.put(key, value);
				}
				listOfAllData.add(data);
				if((filledFields.size()-1) == i){
					++i;
				}
			}
			csvWriter.writeHeader(headers);
			for(Map<String, ? super Object> data : listOfAllData){
				csvWriter.write(data, headers);
			}
		} finally {
			csvWriter.close();
		}
	}

	/**
	 * Method to create CSV String from Collection of FilledQuestionnaireTuple
	 * @return return created string csv
	 * @see #exportCSV(Writer, Collection)
	 */
	public String exportCSVToString(
			Collection<FilledQuestionnaireTuple> filledQuestionnaireTuples)
	throws IOException {
		Writer stringWriter = new StringWriter();
		exportCSV(stringWriter, filledQuestionnaireTuples);
		return stringWriter.toString();
	}

	/**
	 * Import FilledQuestionnaireTuple from CSV file
	 * CSV file structure:
	 * 1st header is name of questionnaire like "Ankieta dentystyczna"
	 * 2nd headers are columns of FilledQuestionnaireTuple to input like
	 * "id,Imię,Nazwisko,Który ząb,Opis bólu"
	 * Rest of file, are data to input.
	 * If there are null entries it should be skipped
	 * Standard separator is comma, CSV file accepts space in names
	 * @param reader with csv, work for both line separators - \n and \r\n
	 * @param filledQuestionnaireTuple questionnaireTuple to input
	 * to get filledFields array
	 * @throws IOException When in IO exception occur
	 * @throws WrongCSVFile When parsing fail
	 */
	public void importCSV(Reader reader,
			FilledQuestionnaireTuple filledQuestionnaireTuple)
	throws IOException, WrongCSVFile {
		ICsvMapReader csvReader = new CsvMapReader(reader, CsvPreference.EXCEL_PREFERENCE);
		try{
			String[] headers0 = csvReader.getCSVHeader(false);
			String[] headers1 = csvReader.getCSVHeader(false);
			filledQuestionnaireTuple.setName(headers0[0]);
			Map<String, String> mapa = null;
			ArrayList<FieldTuple> filledFields = filledQuestionnaireTuple.getFilledFields();
			while((mapa = csvReader.read(headers1)) != null){
				for(FieldTuple fieldTuple : filledFields){
					String key = fieldTuple.getName();
					String value = mapa.get(key);
					if(fieldTuple instanceof FieldTextBoxTuple || fieldTuple instanceof FieldTextBoxDataTuple){
						((FieldTextBoxTuple)fieldTuple).setValue(value);
					}
					else if (fieldTuple instanceof FieldTextAreaTuple || fieldTuple instanceof FieldTextAreaDataTuple){
						((FieldTextAreaTuple)fieldTuple).setValue(value);
					}
					else if (fieldTuple instanceof FieldIntegerBoxTuple || fieldTuple instanceof FieldIntegerBoxDataTuple) {
						try {
							int intValue = Integer.parseInt(value);
							((FieldIntegerBoxTuple)fieldTuple).setValue(intValue);
						} catch (NumberFormatException ex) {
							throw new WrongCSVFile();
						}
					}
				}
			}
			filledQuestionnaireTuple.setFilledFields(filledFields);
		}
		finally{
			csvReader.close();
		}
	}

	/**
	 * Import CSV from string
	 * @see #importCSV(Reader, FilledQuestionnaireTuple)
	 */
	public void importCSVFromString(
			String csv,
			FilledQuestionnaireTuple filledQuestionnaireTuple)
	throws IOException, WrongCSVFile{
		Reader stringReader = new StringReader(csv);
		importCSV(stringReader, filledQuestionnaireTuple);
	}

}
