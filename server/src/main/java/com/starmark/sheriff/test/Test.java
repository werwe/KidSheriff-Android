package com.starmark.sheriff.test;

import com.starmark.sheriff.entity.UserInfo;
import com.starmark.sheriff.pojo.HistoryRequest;
import com.starmark.sheriff.pojo.LinkRequestData;
import com.starmark.sheriff.pojo.Location;
import com.starmark.sheriff.pojo.LocationInfo;
import com.starmark.sheriff.pojo.LocationList;
import com.starmark.sheriff.pojo.UserDataResult;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;
//import com.sun.jersey.api.client.config.ClientConfig;
//import com.sun.jersey.api.client.config.DefaultClientConfig;
//import com.sun.jersey.api.json.JSONConfiguration;
//import com.sun.jersey.core.util.MultivaluedMapImpl;
//import com.sun.jersey.multipart.FormDataBodyPart;
//import com.sun.jersey.multipart.FormDataMultiPart;

public class Test {
	public static void main(String[] args) {
//		Client client = ClientBuilder.newBuilder().build();

		//local
		//URI uri = getLocalBaseURI();
		
		//remote
//		URI uri = getBaseURI();
//
//        WebTarget service = client.target(uri);

		//		System.out.println(service.path("apis").path("hello/name=kk7777").accept(
		//				MediaType.TEXT_PLAIN).get(String.class));
		
		//postRequest(client);
		//linkRequest();
		//updateLocRequest();
		//getUserLocation();
		
		//accountCheckRequst();
		
		//uploadDummyLocations();
//		uploadFile();
	}

	//37.504537, 127.049027
	//37.505606, 127.048461
	//37.505931, 127.048284
	//37.506159, 127.048869
	//37.506884, 127.049003
	//37.506754, 127.050211
	//37.506790, 127.051560
	//37.506925, 127.052279
	//37.507461, 127.053507
	//37.507878, 127.053547
	//37.508546, 127.052987
	//37.509459, 127.051903
	//37.511196, 127.050156
	//37.509203, 127.044249
//	private static void uploadDummyLocations() {
//		ArrayList<Location> list = new ArrayList<Location>();
//		list.add(new Location(37.504537, 127.049027));
//		list.add(new Location(37.505606, 127.048461));
//		list.add(new Location(37.505931, 127.048284));
//		list.add(new Location(37.506159, 127.048869));
//		list.add(new Location(37.506884, 127.049003));
//		list.add(new Location(37.506754, 127.050211));
//		list.add(new Location(37.506790, 127.051560));
//		list.add(new Location(37.506925, 127.052279));
//		list.add(new Location(37.507461, 127.053507));
//		list.add(new Location(37.507878, 127.053547));
//		list.add(new Location(37.508546, 127.052987));
//		list.add(new Location(37.509459, 127.051903));
//		list.add(new Location(37.511196, 127.050156));
//		list.add(new Location(37.509203, 127.044249));
//
//		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
//
//		for(Location l:list)
//		{
//			l.setDate(DateTime.now().toString(fmt));
//			updateLocRequest("werwe@starmark.co.kr",l);
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//
//	}
//
//	private static URI getLocalBaseURI() {
//		return UriBuilder.fromUri(
//				"http://localhost:8881/").build();
//	}
//
//	private static URI getBaseURI() {
//		return UriBuilder.fromUri(
//				"http://kid-sheriff-001.appspot.com/").build();
//	}
//
//	public static void postRequest(Client client)
//	{
//		WebTarget service = client.target(getBaseURI());
//		//MultivaluedMap<String,String> queryParams = new MultivaluedMapImpl();
//        service.queryParam("email", "werwe.test@starmark.com");
//        service.queryParam("pushid", "this_is_push_id 22");
//
//        String result = service.path("apis/regist")
//                .request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
//                .post(Entity.text("entity"),String.class);
//
//
//
//        System.out.println(result);
//	}
//
//	public static void accountCheckRequst()
//	{
////		ClientConfig config = new DefaultClientConfig();
////		config.getFeatures().put(
////				JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
//        Client client = ClientBuilder.newBuilder().build();
//		String account = "werwe.test@gmail.com";
//		WebResource webResource = client
//				.resource("http://kid-sheriff-001.appspot.com/apis/check/account="+account);
//		ClientResponse response = webResource.accept("application/json")
//
//				.get(ClientResponse.class);
//		if (response.getStatus() != 200) {
//			throw new RuntimeException("Failed : HTTP error code : "
//					+ response.getStatus());
//		}
//		UserDataResult output = response.getEntity(UserDataResult.class);
//		System.out.println("check account response .... \n");
//		System.out.println(output);
//	}
//
//	public static void linkRequest()
//	{
//		try {
//			List<String> list = new ArrayList<String>();
//			list.add("werwe.me@gmail.com");
//			list.add("werwe.test@gmail.com");
//			LinkRequestData data =
//				new LinkRequestData("werwe@starmark.co.kr","this is push id",list, UserInfo.CHILD);
//			ClientConfig clientConfig = new DefaultClientConfig();
//			clientConfig.getFeatures().put(
//					JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
//            Client client = ClientBuilder.newBuilder().build();
//
////			WebResource webResource = client
////					.resource("http://localhost:8881/apis/link");
//
//			WebResource webResource = client
//					.resource("http://kid-sheriff-001.appspot.com/apis/link");
//
//
//
//			ClientResponse response = webResource.accept("application/json")
//					.type("application/json").post(ClientResponse.class, data);
//
//			if (response.getStatus() != 200) {
//				throw new RuntimeException("Failed : HTTP error code : "
//						+ response.getStatus());
//			}
//
//			String output = response.getEntity(String.class);
//
//			System.out.println("Server response .... \n");
//			System.out.println(output);
//
////			LinkRequestData response = webResource.accept("application/json")
////					.type("application/json").post(LinkRequestData.class, data);
////
////
////			System.out.println("Server response .... \n");
////			System.out.println(response.toString());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static void updateLocRequest(String emailId, Location loc)
//	{
//		try {
//			LocationInfo info = new LocationInfo();
//			info.setLoc(loc);
//			info.setUserId(emailId);
//
//			ClientConfig clientConfig = new DefaultClientConfig();
//			clientConfig.getFeatures().put(
//					JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
//            Client client = ClientBuilder.newBuilder().build();
//
//			WebResource webResource = client
//					.resource("http://kid-sheriff-001.appspot.com/apis/updateLoc");
//
//			ClientResponse response = webResource.accept("application/json")
//					.type("application/json").post(ClientResponse.class, info);
//
//			if (response.getStatus() != 200) {
//				throw new RuntimeException("Failed : HTTP error code : "
//						+ response.getStatus());
//			}
//			String output = response.getEntity(String.class);
//			System.out.println("Server response .... \n");
//			System.out.println(output);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static void getUserLocation()
//	{
//		try {
//			HistoryRequest reqData = new HistoryRequest();
//			reqData.setRequestorId("werwe.test@gmail.com");
//			reqData.setTargetUserId("werwe.me@gmail.com");
//			reqData.setLimit(3);
//
//			ClientConfig clientConfig = new DefaultClientConfig();
//			clientConfig.getFeatures().put(
//					JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
//            Client client = ClientBuilder.newBuilder().build();
//
//			WebResource webResource = client
//					.resource("http://kid-sheriff-001.appspot.com/apis/getLoc");
//
//			LocationList locations = webResource.accept("application/json")
//					.type("application/json").post(LocationList.class, reqData);
//
//			if(locations != null)
//			{
//				System.out.println("Server response .... \n");
//				System.out.println("result:\n"+locations.getResult());
//				for(Location l : locations.getList())
//					System.out.println("loc:"+l.toString());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static void uploadFile(){
//		   Client client = ClientBuilder.newClient();
//		    WebTarget resource = client.target("http://kid-sheriff-001.appspot.com/apis/uploadImg");
//
//		    FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
//		    formDataMultiPart.field("image", "map.png");
//
//		    FileInputStream input = null;
//			try {
//				input = new FileInputStream(new File("map.png"));
//				System.out.println("file exists");
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//		    FormDataBodyPart bodyPart = new FormDataBodyPart(
//		    		"image",
//		    		input,
//		            MediaType.APPLICATION_OCTET_STREAM_TYPE);
//		    formDataMultiPart.bodyPart(bodyPart);
//
//
//		    String reString = resource.request(MediaType.MULTIPART_FORM_DATA)
//		            .accept(MediaType.TEXT_PLAIN)
//		            .post(String.class, formDataMultiPart);
//
//		    System.out.println(reString);
//	}
}

