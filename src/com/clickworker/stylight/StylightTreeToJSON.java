 package com.clickworker.stylight;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StylightTreeToJSON {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Workbook wb = new XSSFWorkbook("/users/mdittmann/kategoriebaum.xlsx");
		Sheet sheet = wb.getSheetAt(1);
		StylightTreeToJSON schoberToJSON = new StylightTreeToJSON();
		//Sheet transformedSheet = schoberToJSON.reformatSheet(sheet);
		schoberToJSON.excelToModelFromMaster(sheet);		
	}
	
	private Sheet reformatSheet(Sheet sheet) throws FileNotFoundException, IOException{
		Workbook targetWB = new XSSFWorkbook();
		Sheet newSheet = targetWB.createSheet();
		DataFormatter df = new DataFormatter();
		Set<String> uniqueGraphs = new HashSet<String>();
		int rowIndex = 1;
		
		for(Row row : sheet){
			if(row.getRowNum() > 0){
				
				String graph = df.formatCellValue(row.getCell(0));
				String id = df.formatCellValue(row.getCell(2));
				String graphArray [] = graph.split("->");
				
				for(int level = 0; level < graphArray.length; ++level){
					String graphNode = graphArray[level];
					if(uniqueGraphs.add(graphNode)){
						Row newRow = newSheet.createRow(rowIndex);
						Cell cell = newRow.createCell(0);
						Cell idCell = newRow.createCell(2);
						
						switch(level){
						case 0:
							idCell.setCellValue("1000" + id);
							cell.setCellValue(graphArray[0].substring(0, graphArray[0].length() - 1));
							break;
						case 1:
							idCell.setCellValue("2000" + id);
							cell.setCellValue(graphArray[0].substring(0, graphArray[0].length() - 1) + "->" + graphArray[1].substring(0, graphArray[1].length() - 1));
							break;
						case 2:
							idCell.setCellValue("" + id);
							cell.setCellValue(graphArray[0].substring(0, graphArray[0].length() - 1) + "->" + graphArray[1].substring(0, graphArray[1].length() - 1) + "->" + graphArray[2].substring(0, graphArray[2].length() - 1));
							break;
						default:
							System.out.println("Error with Level from Path!");
							break;
						}
						
						rowIndex++;
					}
				}
				
				
			}
			
		}
		//targetWB.write(new FileOutputStream("trans_out.xlsx"));
		
		return newSheet;
	}
	
	private void excelToModel(Sheet sheet){
		DataFormatter df = new DataFormatter();
		ArrayList<StylightNode> nodes_1 = new ArrayList<StylightNode>();
		ArrayList<StylightNode> nodes_2 = new ArrayList<StylightNode>();
		ArrayList<StylightNode> nodes_3 = new ArrayList<StylightNode>();
		ArrayList<StylightNode> nodes_4 = new ArrayList<StylightNode>();
		
		for(Row row : sheet){
			if(row.getRowNum() > 0){
				String graph = df.formatCellValue(row.getCell(0));
				String graphArray [] = graph.split("->");
				
				
				
					String name = graphArray[graphArray.length - 1];
					short level = (short)graphArray.length;
					String desc = df.formatCellValue(row.getCell(3));
					String image = df.formatCellValue(row.getCell(1));
					String id = df.formatCellValue(row.getCell(2));
					String parentName = "";
					System.out.println(id);
					if(id.equals("10221")){
						System.out.println("Found");
					}
					
					StylightNode node = new StylightNode();
					node.setName(name);
					node.setLevel(level);
					node.setDescription(desc.length() > 1 ? desc : "");
					node.setId(Integer.parseInt(id));
					node.setImage(image);
					
					switch(node.getLevel()){
					case 1:
						nodes_1.add(node);
						break;
					case 2:
						nodes_2.add(node);
						parentName = graphArray[graphArray.length - 2];
						setParent(nodes_1, parentName, node);
						break;
					case 3:
						nodes_3.add(node);
						parentName = graphArray[graphArray.length - 2];
						setParent(nodes_2, parentName, node);
						break;
					case 4:
						nodes_4.add(node);
						parentName = graphArray[graphArray.length - 2];
						setParent(nodes_3, parentName, node);
						break;
					default:
						break;
					}
				
				
			}
		}
		int count = nodes_1.size() + nodes_2.size() + nodes_3.size() + nodes_4.size();
		System.out.println("Have " + count + " categories.");
//				
//				switch(node.getLevel()){
//				case 0:
//					parentNodes = null;
//					nodes_0.add(node);
//					break;
//				case 1:
//					nodes_1.add(node);
//					parentNodes = nodes_0;
//					break;
//				case 2:
//					nodes_2.add(node);
//					parentNodes = nodes_1;
//					break;
//				case 3:
//					nodes_3.add(node);
//					parentNodes = nodes_2;
//					break;
//				case 4:
//					nodes_4.add(node);
//					parentNodes = nodes_3;
//					break;
//				default:
//					parentNodes = null;
//					break;
//				}
//				if(parentNodes != null){
//					SchoberNode parent = parentNodes.get(parentNodes.size() > 0 ? parentNodes.size() -1 : 0);
//					parent.addChild(node);
//				}
//			}
//		}
//		System.out.println("0:" + nodes_0.size() + "/1:" + nodes_1.size() + "/2:" + nodes_2.size() + "/3:" + nodes_3.size() + "/4:" + nodes_4.size() + "/5:" + nodes_5.size() + "/6:" + nodes_6.size());
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		System.out.println(gson.toJson(nodes_1));
	}

	private void setParent(ArrayList<StylightNode> nodes, String parentName,
			StylightNode node) {
		boolean parentFound = false;
		for(StylightNode formerNode : nodes){
			if(formerNode.getName().equals(parentName)){
				node.setParent(formerNode.getId());
				formerNode.addChild(node);
				parentFound = true;
				break;
			}
			
		}
		if(!parentFound){
			System.out.println("Parent Item with Name: " + parentName + " not found!");
		}
		
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
		
		short COLUMN_INDEX_TAG_ID = 0;
		short COLUMN_INDEX_LEVEL = 1;
		short COLUMN_INDEX_PARENT = 2;
		short COLUMN_INDEX_NAME_DE = 4;
		short COLUMN_INDEX_EXPL_DE = 5;
		short COLUMN_INDEX_NAME_UK = 6;
		short COLUMN_INDEX_EXPL_UK = 7;
		short COLUMN_INDEX_IMAGE = 8;
		
		
		for(Row row : sheet){
			if(row.getRowNum() > 0){
				
				String id = df.formatCellValue(row.getCell(COLUMN_INDEX_TAG_ID));
				String level = df.formatCellValue(row.getCell(COLUMN_INDEX_LEVEL));
				String parentId = df.formatCellValue(row.getCell(COLUMN_INDEX_PARENT));
				String name = df.formatCellValue(row.getCell(COLUMN_INDEX_NAME_UK));
				String desc = df.formatCellValue(row.getCell(COLUMN_INDEX_EXPL_UK));
				String image = df.formatCellValue(row.getCell(COLUMN_INDEX_IMAGE));
				
				StylightNode node = new StylightNode();
				node.setName(name);
				node.setLevel(Short.parseShort(level));
				node.setDescription(desc.length() > 1 ? desc : "");
				node.setId(Integer.parseInt(id));
				node.setImage(image);
				
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