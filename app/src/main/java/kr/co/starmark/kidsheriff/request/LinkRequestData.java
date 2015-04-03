package kr.co.starmark.kidsheriff.request;

import java.util.List;

import kr.co.starmark.kidsheriff.RegistActivity;


public class LinkRequestData
{
	public String email;
    public String pushid;
    public List<String> linkedAccounts;
    public int whichSide = RegistActivity.CHILD;
}