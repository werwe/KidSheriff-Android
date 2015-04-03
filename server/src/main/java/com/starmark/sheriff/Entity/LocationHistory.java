package com.starmark.sheriff.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.starmark.sheriff.pojo.Location;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationHistory {
	@Id Long id;
	@Index
	Ref<UserInfo> userRef;
	Location loc;
	
	public LocationHistory(Ref<UserInfo> ref,Location loc)
	{
		userRef = ref;
		this.loc = loc;
	}

}
