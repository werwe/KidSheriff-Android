package com.starmark.sheriff.entity;

import lombok.Data;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Data
@Entity
@Index
public class ImageStore {
	@Id Long id;
	
	@Index
	Ref<UserInfo> user;
	
	@Index
	String date;
	
	double lat;
	double lng;
	
	String blobKey;
}
