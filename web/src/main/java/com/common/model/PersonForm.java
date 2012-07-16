package com.common.model;

public class PersonForm {

	private String name;
	private String city;
	private int id = -1;
	
	public PersonForm(int id,String name, String city ) {
		this.name = name;
		this.city = city;
		this.id = id;
	}
	public PersonForm() {
		// TODO Auto-generated constructor stub
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
