package com.starmark.sheriff;

//import static com.starmark.sheriff.OfyService.ofy;
//import static com.starmark.sheriff.OfyService.factory;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.cmd.LoadType;
import com.starmark.resource.Entity;
import com.starmark.resource.FileMeta;
import com.starmark.resource.FileUrl;
import com.starmark.sheriff.entity.ImageStore;
import com.starmark.sheriff.entity.LinkInfo;
import com.starmark.sheriff.entity.LocationHistory;
import com.starmark.sheriff.entity.UserInfo;
import com.starmark.sheriff.pojo.HistoryRequest;
import com.starmark.sheriff.pojo.ImageStoreInfoList;
import com.starmark.sheriff.pojo.LinkRequestData;
import com.starmark.sheriff.pojo.Location;
import com.starmark.sheriff.pojo.LocationInfo;
import com.starmark.sheriff.pojo.LocationList;
import com.starmark.sheriff.pojo.UserDataResult;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import lombok.extern.java.Log;


@Log
@Path("/apis")
public class SheriffServer {

    private static final String API_KEY = System.getProperty("gcm.api.key");
	Sender mSender = new Sender(API_KEY);
	
	@GET
	@Path("/pushNoti/name={name}")
	public Response push(@PathParam("name") final String name) throws IOException {
		StringBuilder builder = new StringBuilder();
		String requestorUserId = name;
		
		builder.append("request user:"+requestorUserId+"\n");
		
		Objectify ofy = OfyService.ofy();
		

		List<LinkInfo> list = 
			ofy.load().type(LinkInfo.class).filter("linkedAccount", requestorUserId)
			.list();
		ArrayList<Key<UserInfo>> keys = new ArrayList<Key<UserInfo>>(list.size());
		for(LinkInfo info:list)
			keys.add(info.getKey());
		
		LoadType<UserInfo> loadType = ofy.load().type(UserInfo.class);
		
		
		List<String> pushIdList = new ArrayList<String>();
		for(Key<UserInfo> key:keys)
		{
			List<UserInfo> infos = loadType.filterKey(key).list();
			if(infos != null && infos.size() > 0)
			{
				builder.append("target user:"+infos.get(0).getEmail()+"\n");
				pushIdList.add(infos.get(0).getPushId());
			}
		}
			
		Message message = new Message.Builder()
		.addData("msg", "alert").build();

		MulticastResult multiResult = mSender.send(message, pushIdList, 5);
		 		 
		if (multiResult != null) {
			List<com.google.android.gcm.server.Result> resultList = multiResult.getResults();
			for (com.google.android.gcm.server.Result result : resultList) {
				builder.append(result.getMessageId()+"\n");
			}
		}
		
		return Response.ok(builder.toString()).build();
	}
	
	static
	{
		log.setLevel(Level.WARNING);
	}
	
	//test method
	@GET
	@Path("/hello/name={name}")
	@Produces(MediaType.TEXT_PLAIN)
	public String hello(@PathParam("name") final String name) {
		Result<Key<UserInfo>> result = OfyService.ofy().save()
				.entity(new UserInfo("werwe222@starmark.co.kr", "testPushId",UserInfo.CHILD));
		if(result == null) log.fine("not file");
		else log.fine("fine ^^");
		//else if(result == null) log.log(Level.WARNING, "result is :"+result.now().getId());
		return "Hello, " + name;
	}

	//test method
	@POST
	@Path("/regist")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public String regist(MultivaluedMap<String, String> formParams) {
		String email = formParams.getFirst("email");
		String pushid = formParams.getFirst("pushid");
		return email + "/" + pushid;
	}
	
	@GET
	@Path("/check/account={account}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkAccount(@PathParam("account") final String account) {
		Objectify ofy = OfyService.ofy();
		UserInfo info = new UserInfo();
		info.setEmail(account);
		Key<UserInfo> userKey = Key.create(UserInfo.class, account);
		List<UserInfo> userList = ofy.load().type(UserInfo.class).filterKey(userKey).list();
		
		UserDataResult data = new UserDataResult();
		if(userList == null || userList.size() == 0)
		{
			data.setResult("notExist");
			return Response.status(200).entity(data).build();
		}
		data.setResult("exist");
		data.setEmail(account);
		data.setWhichSide(userList.get(0).getWhichSide());
		List<LinkInfo> linkedAccounts = ofy.load().type(LinkInfo.class).filter("key",userKey).list();
		int acLength = linkedAccounts.size();
		
		for(int i = 0 ; i < acLength ; i++)
			data.getLinkedAccounts().add(linkedAccounts.get(i).getLinkedAccount());
		
		return Response.status(200).entity(data).build();
	}

	@POST
	@Path("/link")
	@Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	public Response requestRegist(LinkRequestData param) {
			String userEmail = param.getEmail();
			String pushId = param.getPushid();
			List<String> parent = param.getLinkedAccounts();
			int whichSide = param.getWhichSide();
			Objectify ofy = OfyService.ofy();
			UserInfo info = new UserInfo(userEmail,pushId,whichSide);
			Key<UserInfo> userKey = Key.create(info);
			ofy.save().entities(info);
			
			LoadType<LinkInfo> linkList = ofy.load().type(LinkInfo.class);
			int pLength = parent.size();
			
			StringBuilder builder = new StringBuilder();
			for(int i = 0 ; i < pLength ; i++)
			{
				List<LinkInfo> list = 
						linkList.filter("linkedAccount", parent.get(i))
						.filter("key", userKey)
						.list();

				if (list == null || list.size() == 0)
				{
					ofy.save().entities(new LinkInfo(userKey,parent.get(i)));
				}
				else
				{
					//LinkInfo query =list.get(0);
					//ofy.save().entities(query);
				}
//				{
//				    ofy.transact(new VoidWork() { 
//				      @Override public void vrun() {
//				        ofy.save.entity(newPerson).now();
//				      }
//				}
			}
			
		return Response.status(200).entity("success").build();
	}
	
	@POST
	@Path("/updateLoc")
	@Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	public Response updateLocation(LocationInfo param) {
			StringBuilder builder = new StringBuilder();
			String userId = param.getUserId();
			Location loc = param.getLoc();
			Objectify ofy = OfyService.ofy();
			Key<UserInfo> userKey = Key.create(UserInfo.class, userId);
			
			builder.append(userKey.toString()).append(" / ");
			List<UserInfo> userList = ofy.load().type(UserInfo.class).filterKey(userKey).list();
			
			if(userList != null && userList.size() > 0)
			{
				Ref<UserInfo> refUser = Ref.create(userList.get(0));
			
				LocationHistory history = new LocationHistory(refUser,loc);
				ofy.save().entities(history);
				
				builder.append(loc.toString());
			}
			else
			{
				return Response.status(200).entity("no user infomation : " + builder.toString()).build();
			}	
		return Response.status(200).entity("success : " + builder.toString()).build();
	}
	
	@POST
	@Path("/getLoc")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public LocationList getLocation(HistoryRequest request) {
			StringBuilder builder = new StringBuilder();
			LocationList locations = new LocationList();
			
			String requestorUserId = request.getRequestorId();
			String targetUserId = request.getTargetUserId();
			
			builder.append("request user:"+requestorUserId+"\n");
			builder.append("target user:"+targetUserId+"\n");
			
			Objectify ofy = OfyService.ofy();
			
			Key<UserInfo> targetKey = Key.create(UserInfo.class,targetUserId);
			List<LinkInfo> list = 
				ofy.load().type(LinkInfo.class).filter("linkedAccount", requestorUserId)
				.filter("key", targetKey)
				.list();

			if (list == null || list.size() == 0)
			{
				builder.append("not linked user");
				locations.setResult(builder.toString());
				return locations;
			}
			builder.append("linked user\n");

			UserInfo user = new UserInfo();
			user.setEmail(targetUserId);
			List<LocationHistory> locList = ofy.load()
					.type(LocationHistory.class)
					.filter("userRef",user)
					.limit(request.getLimit())
					.list();
			if(locList != null && locList.size() > 0)
			{
				int size = locList.size();
				for(int i = 0 ; i < size ; i++)
				{
					locations.addLocation(locList.get(i).getLoc());
				}
			}
			locations.setResult(builder.toString());
		return locations;
	}
	

	
	/**
	 * Blob Store Service
	 */
    private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    private final BlobInfoFactory blobInfoFactory = new BlobInfoFactory();

    /* step 1. get a unique url */

    @GET
    @Path("/file/url")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCallbackUrl() {
        String url = blobstoreService.createUploadUrl("/apis/file");
        return Response.ok(new FileUrl(url)).build();
    }

    /* step 2. post a file */

    @POST
    @Path("/file")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response post(@Context HttpServletRequest req, @Context HttpServletResponse res) throws IOException, URISyntaxException {
    	
    	String emailId = req.getParameter("emailId");
    	double lat = Double.parseDouble(req.getParameter("lat"));
    	double lng = Double.parseDouble(req.getParameter("lng"));
    	String date = req.getParameter("date");
    	Ref<UserInfo> userRef = Ref.create(Key.create(UserInfo.class, emailId));
    	
    	
        Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        
        StringBuilder builder = new StringBuilder();
        builder.append("emailId:"+req.getParameter("emailId")+"\n");
        //String emailId = req.getParameter("emailId");
        for(String key:blobs.keySet())
        	builder.append("key:"+key+"\n");
        BlobKey blobKey = blobs.get("file");
        //res.sendRedirect("/apis/file/" + blobKey.getKeyString() + "/meta");

        builder.append("blobKey is " + blobKey + "\n");
        if(blobKey == null)
        {
        	return Response.ok(new Entity(builder.toString(),null),MediaType.APPLICATION_JSON).build();
        }
        BlobInfo info = blobInfoFactory.loadBlobInfo(blobKey);
        String name = info.getFilename();
        long size = info.getSize();
        String url = "/apis/file/" + blobKey.getKeyString();

        ImageStore store = new ImageStore();
        store.setUser(userRef);
        store.setLat(lat);
        store.setLng(lng);
        store.setDate(date);
        store.setBlobKey(blobKey.getKeyString());
        
        OfyService.ofy().save().entities(store);
        /**
         * Preview image
         */
        //ImagesService imagesService = ImagesServiceFactory.getImagesService();
        //ServingUrlOptions.Builder.withBlobKey(blobKey).crop(true).imageSize(80);
        //int sizePreview = 80;
        //String urlPreview = imagesService
        //           .getServingUrl(ServingUrlOptions.Builder.withBlobKey(blobKey)
        //       .crop(true).imageSize(sizePreview));

        FileMeta meta = new FileMeta(name, size, url);
        
        

        List<FileMeta> metas = Lists.newArrayList(meta);
        
        
        Entity entity = new Entity(builder.toString(),metas);
        return Response.ok(entity, MediaType.APPLICATION_JSON).build();
    }
    
    //apis/getImages/name
	@GET
	@Path("/getImages/name={name}")
	public Response getImages(@PathParam("name") final String name) {
			StringBuilder builder = new StringBuilder();
			ImageStoreInfoList infos = new ImageStoreInfoList(name);
			
			builder.append("name:"+name).append("\n");
			Objectify ofy = OfyService.ofy();


			UserInfo user = new UserInfo();
			user.setEmail(name);
			List<ImageStore> imageList = ofy.load()
					.type(ImageStore.class)
					.order("-date")
					.filter("user",user)
					.limit(1)
					.list();
			
			//builder.append(imageList);
			if(imageList != null && imageList.size() > 0)
			{
				int size = imageList.size();
				for(int i = 0 ; i < size ; i++)
				{
					infos.add(imageList.get(i));
				}
			}
			infos.result = builder.toString();
			
		return Response.ok(infos, MediaType.APPLICATION_JSON).build();
	}

    /* step 3. redirected to the meta info */

    @GET
    @Path("/file/{key}/meta")
    @Produces(MediaType.APPLICATION_JSON)
    public Response redirect(@PathParam("key") String key) throws IOException {
        BlobKey blobKey = new BlobKey(key);
        BlobInfo info = blobInfoFactory.loadBlobInfo(blobKey);

        String name = info.getFilename();
        long size = info.getSize();
        String url = "/apis/file/" + key; 
        FileMeta meta = new FileMeta(name, size, url);

        List<FileMeta> metas = Lists.newArrayList(meta);
        GenericEntity<List<FileMeta>> entity = new GenericEntity<List<FileMeta>>(metas) {};
        return Response.ok(entity).build();
    }

    /* step 4. download the file */

    @GET
    @Path("/file/{key}")
    public Response serve(@PathParam("key") String key, @Context HttpServletResponse response) throws IOException {
        BlobKey blobKey = new BlobKey(key);
        final BlobInfo blobInfo = blobInfoFactory.loadBlobInfo(blobKey);
        response.setHeader("Content-Disposition", "attachment; filename=" + blobInfo.getFilename());
        BlobstoreServiceFactory.getBlobstoreService().serve(blobKey, response);
        return Response.ok().build();
    }

    /* step 5. delete the file */

    @DELETE
    @Path("/file/{key}")
    public Response delete(@PathParam("key") String key) {
        Status status;
        try {
            blobstoreService.delete(new BlobKey(key));
            status = Status.OK;
        } catch (BlobstoreFailureException bfe) {
            status = Status.NOT_FOUND;
        }
        return Response.status(status).build();
    }
    
    //apis/deleteAllImageStore
	@GET
	@Path("/deleteAllImageStore")
	public Response deleteAllImageStore() {
			Objectify ofy = OfyService.ofy();
			List<Key<ImageStore>> keys = ofy.load().type(ImageStore.class).keys().list();
			ofy.delete().keys(keys).now();

		return Response.ok("remove " + keys.size() +" entities").build();
	}
	
    //apis/deleteAllBlobs
	@GET
	@Path("/deleteAllBlobs")
	public Response deleteAllBlobs() {
			Objectify ofy = OfyService.ofy();
			List<Key<ImageStore>> keys = ofy.load().type(ImageStore.class).keys().list();
			ofy.delete().keys(keys).now();

		return Response.ok("remove " + keys.size() +" entities").build();
	}
}

//
//@Api(name = "messaging", version = "v1", namespace = @ApiNamespace(ownerDomain = "sheriff.starmark.com", ownerName = "sheriff.starmark.com", packagePath=""))
//public class MessagingEndpoint {
//    private static final Logger log = Logger.getLogger(MessagingEndpoint.class.getName());
//
//    /** Api Keys can be obtained from the google cloud console */
//    private static final String API_KEY = System.getProperty("gcm.api.key");
//
//    /**
//     * Send to the first 10 devices (You can modify this to send to any number of devices or a specific device)
//     *
//     * @param message The message to send
//     */
//    public void sendMessage(@Named("message") String message) throws IOException {
//        if(message == null || message.trim().length() == 0) {
//            log.warning("Not sending message because it is empty");
//            return;
//        }
//        // crop longer messages
//        if (message.length() > 1000) {
//            message = message.substring(0, 1000) + "[...]";
//        }
//        Sender sender = new Sender(API_KEY);
//        Message msg = new Message.Builder().addData("message", message).build();
//        List<RegistrationRecord> records = ofy().load().type(RegistrationRecord.class).limit(10).list();
//        for(RegistrationRecord record : records) {
//            com.google.android.gcm.server.Result result = sender.send(msg, record.getRegId(), 5);
//            if (result.getMessageId() != null) {
//                log.info("Message sent to " + record.getRegId());
//                String canonicalRegId = result.getCanonicalRegistrationId();
//                if (canonicalRegId != null) {
//                    // if the regId changed, we have to update the datastore
//                    log.info("Registration Id changed for " + record.getRegId() + " updating to " + canonicalRegId);
//                    record.setRegId(canonicalRegId);
//                    ofy().save().entity(record).now();
//                }
//            } else {
//                String error = result.getErrorCodeName();
//                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
//                    log.warning("Registration Id " + record.getRegId() + " no longer registered with GCM, removing from datastore");
//                    // if the device is no longer registered with Gcm, remove it from the datastore
//                    ofy().delete().entity(record).now();
//                }
//                else {
//                    log.warning("Error when sending message : " + error);
//                }
//            }
//        }
//    }
//}
