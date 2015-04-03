package com.starmark.sheriff.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Location {
	private String date;
	private double lat;
	private double lng;
	
	public Location(double lat, double lng)
	{
		this.lat = lat;
		this.lng = lng;
	}
}