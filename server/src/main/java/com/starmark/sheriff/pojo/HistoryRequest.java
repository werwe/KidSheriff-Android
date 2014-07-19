package com.starmark.sheriff.pojo;

import lombok.Data;

@Data
public class HistoryRequest {
	String requestorId;
	String targetUserId;
	int limit = 0;
}
