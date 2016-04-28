package excel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;

import exceptions.CharInvalideException;
import Parser.*;
import Bdd.*;
import Bdd.Bdd.content;




public class ExcelWriter {
	
	
	public static void writer(String filepath, String[] chemin, Bdd base) {
		try {
			
			
			Pattern regex1 = Pattern.compile(".*/");
			Matcher m = regex1.matcher(filepath);
			if (m.find()){
				String folderpath = m.group(0);
				File folders = new File(folderpath);
				folders.mkdirs();
			}
			
			String xlsfile = filepath+".xls";
			
			FileOutputStream fileOut = new FileOutputStream(xlsfile);
			HSSFWorkbook workbook = new HSSFWorkbook();
			
			

			String cleft;
			content contenus;
			
			for (Entry<String, content> entry : base.getContenus())
			{
				cleft = entry.getKey(); //"mitochondrie", "chloroplaste", "general"
				contenus = entry.getValue(); //un objet content équipé de toute les fonction que vous appliquiez a la base avant
				
				HSSFSheet worksheet = workbook.createSheet(cleft);
	
				List<HSSFRow> rowlist = new ArrayList<HSSFRow>();
				
				
				for (int i = 0; i <80; i++){
					rowlist.add(worksheet.createRow(i));
					
					for(int j = 0; j<20; j++){
						rowlist.get(i).createCell(j);
					}
				}
				
				HSSFDataFormat dataFormat = workbook.createDataFormat();
				
				CellStyle intStyle = workbook.createCellStyle();
				intStyle.setDataFormat(dataFormat.getBuiltinFormat("0"));
				
				CellStyle floatStyle = workbook.createCellStyle();
				floatStyle.setDataFormat(dataFormat.getBuiltinFormat("0.00"));
				
				
				
				// ligne 1 
				rowlist.get(0).getCell(0).setCellValue("Nom");
				String filename = "";
				if (chemin[3] != null && chemin[3] != "" ) {
					filename = chemin[3];
				}
				else if (chemin[2] != null && chemin[2] != "" ) {
					filename = chemin[2];
				}
				else if (chemin[1] != null && chemin[1] != "") {
					filename = chemin[1];
				}
				else {
					filename = chemin[0];
				}
				
				
				rowlist.get(0).getCell(1).setCellValue(filename);
				
				
				
				//ligne 2
				rowlist.get(1).getCell(0).setCellValue("Chemin");			
				rowlist.get(1).getCell(1).setCellValue(chemin[0]);			
				rowlist.get(1).getCell(2).setCellValue(chemin[1]);
				rowlist.get(1).getCell(3).setCellValue(chemin[2]);
				rowlist.get(1).getCell(4).setCellValue(chemin[3]);
				
				//ligne 3
				rowlist.get(2).getCell(0).setCellValue("Nb CDS");
				rowlist.get(2).getCell(1).setCellStyle(intStyle);
				rowlist.get(2).getCell(1).setCellValue(contenus.get_nb_CDS());
				
				
				
				//ligne 4
				rowlist.get(3).getCell(0).setCellValue("Nb trinucleotides");
				rowlist.get(3).getCell(1).setCellStyle(intStyle);
				rowlist.get(3).getCell(1).setCellValue(contenus.get_nb_trinucleotides()/3);
				rowlist.get(3).getCell(8).setCellValue("Nb dinucleotides");
				rowlist.get(3).getCell(9).setCellStyle(intStyle);
				rowlist.get(3).getCell(9).setCellValue(contenus.get_nb_dinucleotides()/2);
			
				
				//ligne 5
				rowlist.get(4).getCell(0).setCellValue("Nb CDS non traités");
				rowlist.get(4).getCell(1).setCellStyle(intStyle);
				rowlist.get(4).getCell(1).setCellValue(contenus.get_nb_CDS_non_traites());
				
	
				
				//ligne 7
				rowlist.get(6).getCell(0).setCellValue("Trinucléotides");			
				rowlist.get(6).getCell(1).setCellValue("Nb Ph0");			
				rowlist.get(6).getCell(2).setCellValue("Pb Ph0");		
				rowlist.get(6).getCell(3).setCellValue("Nb Ph1");		
				rowlist.get(6).getCell(4).setCellValue("Pb Ph1");			
				rowlist.get(6).getCell(5).setCellValue("Nb Ph2");			
				rowlist.get(6).getCell(6).setCellValue("Pb Ph2");			
				rowlist.get(6).getCell(8).setCellValue("Dinucléotides");			
				rowlist.get(6).getCell(9).setCellValue("Nb Ph0");			
				rowlist.get(6).getCell(10).setCellValue("Pb Ph0");			
				rowlist.get(6).getCell(11).setCellValue("Nb Ph1");
				rowlist.get(6).getCell(12).setCellValue("Pb Ph1");
				
				rowlist.get(71).getCell(0).setCellValue("Total");
				
				
				//on remplit les phases nombres des trinucléotides
				StringBuilder triplet = new StringBuilder("---");
				for (int j=0; j< 4; j++){
					triplet.setCharAt(0, Bdd.charOfNucleotideInt(j));
					for (int k=0; k< 4; k++){
						triplet.setCharAt(1, Bdd.charOfNucleotideInt(k));
						for (int l=0; l< 4; l++){
							int trinucleotide = l+4*k+16*j+7;
							triplet.setCharAt(2, Bdd.charOfNucleotideInt(l));
							rowlist.get(trinucleotide).getCell(0).setCellValue(triplet.toString()); //on remplit le nom des trinucléotides
							for (int i = 0; i<3; i++){
								rowlist.get(trinucleotide).getCell(1+2*i).setCellStyle(intStyle);
								rowlist.get(trinucleotide).getCell(1+2*i).setCellValue((double)(contenus.get_tableautrinucleotides(i,j,k,l)));
							}
						}
					}
				}
				
				//on remplit les phases nombres  des dinucléotides
				StringBuilder couple = new StringBuilder("--");
				for (int j=0; j< 4; j++){
					couple.setCharAt(0, Bdd.charOfNucleotideInt(j));
					for (int k=0; k< 4; k++){
						couple.setCharAt(1, Bdd.charOfNucleotideInt(k));
						int dinucleotide = k+4*j+7;
						rowlist.get(dinucleotide).getCell(8).setCellValue(couple.toString()); //on remplit le nom des dinucléotides
						for (int i = 0; i<2; i++){
							rowlist.get(dinucleotide).getCell(9+2*i).setCellStyle(intStyle);
							rowlist.get(dinucleotide).getCell(9+2*i).setCellValue((double)(contenus.get_tableaudinucleotides(i,j,k)));
						}
					}
				}
				
				//on remplit les totaux entiers
				//trinucleotides
				for(int i = 0; i<3;i++){
					double tmp = 0;
					for (int j = 0; j<64;j++){
						tmp = tmp + (rowlist.get(j+7).getCell(1+2*i).getNumericCellValue());	
					}
					rowlist.get(71).getCell(1+2*i).setCellStyle(intStyle);
					rowlist.get(71).getCell(1+2*i).setCellValue(tmp);
					
				}
				//dinucleotides
				for(int i = 0; i<2;i++){
					double tmp = 0;
					for (int j = 0; j<16;j++){
						tmp = tmp + (rowlist.get(j+7).getCell(9+2*i).getNumericCellValue());	
					}
					rowlist.get(23).getCell(9+2*i).setCellStyle(intStyle);
					rowlist.get(23).getCell(9+2*i).setCellValue(tmp);
					
				}
				
				
				//on remplit les phases probabilités
				//trinucleotides
				for (int i =0; i<3; i++){
					double total = rowlist.get(71).getCell(1+2*i).getNumericCellValue();
					if (total != 0){
						for (int j = 0; j<64; j++){
							rowlist.get(j+7).getCell(2+2*i).setCellStyle(floatStyle);
							double tmp = rowlist.get(j+7).getCell(1+2*i).getNumericCellValue();
							rowlist.get(j+7).getCell(2+2*i).setCellValue(100*tmp/total);
						}
					}
				}
				//dinucleotides
				for (int i =0; i<2; i++){
					double total = rowlist.get(23).getCell(9+2*i).getNumericCellValue();
					if (total != 0){
						for (int j = 0; j<16; j++){
							rowlist.get(j+7).getCell(10+2*i).setCellStyle(floatStyle);
							double tmp = rowlist.get(j+7).getCell(9+2*i).getNumericCellValue();
							rowlist.get(j+7).getCell(10+2*i).setCellValue(100*tmp/total);
						}
					}
				}
				
				//on remplit les totaux flottants
				//trinucleotides
				for(int i = 0; i<3;i++){
					double tmp = 0;
					for (int j = 0; j<64;j++){
						tmp = tmp + (rowlist.get(j+7).getCell(2+2*i).getNumericCellValue());	
					}
					rowlist.get(71).getCell(2+2*i).setCellStyle(intStyle);
					rowlist.get(71).getCell(2+2*i).setCellValue(tmp);
					
				}
				//dinucleotides
				for(int i = 0; i<2;i++){
					double tmp = 0;
					for (int j = 0; j<16;j++){
						tmp = tmp + (rowlist.get(j+7).getCell(10+2*i).getNumericCellValue());	
					}
					rowlist.get(23).getCell(10+2*i).setCellStyle(intStyle);
					rowlist.get(23).getCell(10+2*i).setCellValue(tmp);
					
				}
				
				//autosize column 
				for (int i = 0; i<73; i++){
					for (int j = 0; j < rowlist.get(i).getLastCellNum();j++) {
						worksheet.autoSizeColumn(j);
					}
				}
			
			}
			
			
			
			workbook.write(fileOut);
			workbook.close();
			fileOut.flush();
			fileOut.close();

			
			base.exportBase(filepath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CharInvalideException e) {
			e.printStackTrace();
		}

	}
}
