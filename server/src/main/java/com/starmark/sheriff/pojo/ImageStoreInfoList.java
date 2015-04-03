package com.starmark.sheriff.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import com.starmark.sheriff.entity.ImageStore;


@Data
public class ImageStoreInfoList {
	
	public String result;
	String userId;
	
	List<ImageStoreInfo> list = null;
	
	public ImageStoreInfoList()
	{
		
	}
	
	public ImageStoreInfoList(String userid)
	{
		this.userId = userid;
	}

	public void add(ImageStore imageStore) {
		if(list == null) list = new ArrayList<ImageStoreInfo>();
		ImageStoreInfo info = new ImageStoreInfo();
		info.Date = imageStore.getDate();
		info.imgUrl = imageStore.getBlobKey();
		info.lat =  imageStore.getLat();
		info.lng = imageStore.getLng();
		list.add(info);
		
	}
	

}
