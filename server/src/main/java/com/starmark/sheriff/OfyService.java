package com.starmark.sheriff;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.starmark.sheriff.entity.ImageStore;
import com.starmark.sheriff.entity.LinkInfo;
import com.starmark.sheriff.entity.LocationHistory;
import com.starmark.sheriff.entity.UserInfo;

public class OfyService {
    static {
        ofy().factory().register(LinkInfo.class);
        ofy().factory().register(LocationHistory.class);
        ofy().factory().register(UserInfo.class);
        ofy().factory().register(ImageStore.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}