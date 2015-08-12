 package com.clickworker.rewe;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ReweTreeToJSON {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Workbook wb = new XSSFWorkbook("/users/mdittmann/kategoriebaum.xlsx");
		Sheet sheet = wb.getSheetAt(0);
		ReweTreeToJSON schoberToJSON = new ReweTreeToJSON();
		schoberToJSON.excelToModelFromMaster(sheet);		
	}
		
	private void setParentbyID(ArrayList<StylightNode> nodes, int parentId,
			StylightNode node) {
		boolean parentFound = false;
		for(StylightNode formerNode : nodes){
			if(formerNode.getId() == parentId){
				node.setParent(formerNode.getId());
				formerNode.addChild(node);
				parentFound = true;
				break;
			}
			
		}
		if(!parentFound){
			System.out.println("Parent Item with Id: " + parentId + " not found!");
		}
		
	}
	
	private void excelToModelFromMaster(Sheet sheet){
		DataFormatter df = new DataFormatter();
		ArrayList<StylightNode> nodes_1 = new ArrayList<StylightNode>();
		ArrayList<StylightNode> nodes_2 = new ArrayList<StylightNode>();
		ArrayList<StylightNode> nodes_3 = new ArrayList<StylightNode>();
		ArrayList<StylightNode> nodes_4 = new ArrayList<StylightNode>();
		
		HashMap<Integer, Integer> idLevelMap = new HashMap<Integer, Integer>();
		
		short COLUMN_INDEX_TAG_ID = 0;
		short COLUMN_INDEX_PARENT = 1;
		short COLUMN_INDEX_NAME_DE = 3;
		short COLUMN_INDEX_EXPL_DE = 5;
		short COLUMN_INDEX_NAME_UK = 4;
		short COLUMN_INDEX_EXPL_UK = 6;
		//short COLUMN_INDEX_IMAGE = 0;
		
		for(Row row : sheet){
			if(row.getRowNum() > 0){
				
				String id = df.formatCellValue(row.getCell(COLUMN_INDEX_TAG_ID));
				Short level;
				String parentId = df.formatCellValue(row.getCell(COLUMN_INDEX_PARENT));
				
				if (parentId.equals("")) {
					
					// Level 0 Node
					
					parentId = "";
					idLevelMap.put(Integer.parseInt(id), 1);
					level = 1;
					
				} else {
					
					Integer parentLevel = idLevelMap.get(Integer.parseInt(parentId));
					idLevelMap.put(Integer.parseInt(id), parentLevel + 1);
					level = (short) (parentLevel + 1);
				}
				
				String name = df.formatCellValue(row.getCell(COLUMN_INDEX_NAME_DE));
				String desc = df.formatCellValue(row.getCell(COLUMN_INDEX_EXPL_DE));
				//String image = df.formatCellValue(row.getCell(COLUMN_INDEX_IMAGE));
				
				StylightNode node = new StylightNode();
				node.setName(name);
				node.setLevel(level);
				node.setDescription(desc.length() > 1 ? desc : "");
				node.setId(Integer.parseInt(id));
				//node.setImage(image);
				
				switch(node.getLevel()){
				case 1:
					nodes_1.add(node);
					break;
				case 2:
					nodes_2.add(node);
					setParentbyID(nodes_1, Integer.parseInt(parentId), node);
					break;
				case 3:
					nodes_3.add(node);
					setParentbyID(nodes_2, Integer.parseInt(parentId), node);
					break;
				case 4:
					nodes_4.add(node);
					setParentbyID(nodes_3, Integer.parseInt(parentId), node);
					break;
				default:
					break;
				}
			}
			
		}
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		System.out.println(gson.toJson(nodes_1));
	}

}