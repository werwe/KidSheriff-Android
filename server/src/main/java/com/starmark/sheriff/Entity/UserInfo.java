package com.starmark.sheriff.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
	public static final int CHILD = 1;
	public static final int PARENT = 2;
	
	@Id
	@Index
	private String email;
	private String pushId;
	private int whichSide = CHILD;
	
	public UserInfo(String userId)
	{
		this.email = userId;
	}
}
