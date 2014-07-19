package com.starmark.sheriff.entity;

import lombok.Data;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Data
@Entity
public class LinkInfo{
	@Id 
	Long id;
	
	@Index
	Key<UserInfo> key;
	@Index
	String linkedAccount;

	private LinkInfo(){}
	public LinkInfo(Key<UserInfo> key,String account)
	{
		this.key = key;
		this.linkedAccount = account;
	}
}
