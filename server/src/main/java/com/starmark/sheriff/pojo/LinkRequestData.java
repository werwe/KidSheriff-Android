package com.starmark.sheriff.pojo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.starmark.sheriff.entity.UserInfo;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LinkRequestData
{
	private String email;
	private String pushid;
	private List<String> linkedAccounts;
	private int whichSide = UserInfo.CHILD;
}