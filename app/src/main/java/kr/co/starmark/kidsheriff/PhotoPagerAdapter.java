package kr.co.starmark.kidsheriff;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import kr.co.starmark.kidsheriff.resource.FileMeta;
import kr.co.starmark.kidsheriff.resource.ImageStoreInfo;

public class PhotoPagerAdapter extends PagerAdapter {

        private List<ImageStoreInfo> mMetaList;
        private LayoutInflater mInflater;
        private ImageLoader mImageLoader;
        public PhotoPagerAdapter(Context c){
            super();
            mInflater = LayoutInflater.from(c);
            mImageLoader = VolleySingleton.getInstance().getImageLoader();
            mMetaList = new ArrayList<ImageStoreInfo>();
        }

        @Override
        public int getCount() {
            return mMetaList.size();
        }
 
        @Override
        public Object instantiateItem(ViewGroup pager, int position) {
            NetworkImageView imgView = new NetworkImageView(pager.getContext());
            String url = "http://kid-sheriff-appspot.com/apis/file/" + mMetaList.get(position).getImgUrl();
            imgView.setImageUrl(url,mImageLoader);
            pager.addView(imgView, 0);
            return imgView;
        }
 
        @Override
        public void destroyItem(ViewGroup pager, int position, Object view) {
            pager.removeView((View) view);
        }
         
        @Override
        public boolean isViewFromObject(View pager, Object obj) {
            return pager == obj; 
        }
 
        @Override public void restoreState(Parcelable arg0, ClassLoader arg1) {}
        @Override public Parcelable saveState() { return null; }
        @Override public void startUpdate(ViewGroup arg0) {}
        @Override public void finishUpdate(ViewGroup arg0) {}

    public void setData(List<ImageStoreInfo> list) {
        mMetaList = list;
    }
}