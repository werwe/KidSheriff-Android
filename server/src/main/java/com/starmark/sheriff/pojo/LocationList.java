package com.starmark.sheriff.pojo;

import java.util.ArrayList;

import lombok.Data;

@Data
public class LocationList {
	String result;
	ArrayList<Location> list = new ArrayList<Location>(20);
	
	public void addLocation(Location loc)
	{
		list.add(loc);
	}
}
