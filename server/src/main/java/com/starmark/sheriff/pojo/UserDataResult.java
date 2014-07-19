package com.starmark.sheriff.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class UserDataResult {
	private String result;
	private String email;
	private int whichSide = 0;
	private List<String> linkedAccounts = new ArrayList<String>();
}
