package com.clickworker.rewe;

import java.util.ArrayList;

public class StylightNode {
	
	private String name = "";
	private String description = "";
	private short level = -1;
	private String image = "";
	private int id = 0;
	private int parent = 0;
	private ArrayList<StylightNode> children = null;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name.replaceAll("\\+", "").trim();
	}
	public short getLevel() {
		return level;
	}
	public void setLevel(short level) {
		this.level = level;
	}
	public ArrayList<StylightNode> getChildren() {
		return children;
	}
	public boolean addChild(StylightNode child){
		if(this.children == null){
			this.children = new ArrayList<StylightNode>();
		}
		return this.children.add(child);
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getParent() {
		return parent;
	}
	public void setParent(int parent) {
		this.parent = parent;
//		parent.addChild(this);
	}
	
}
