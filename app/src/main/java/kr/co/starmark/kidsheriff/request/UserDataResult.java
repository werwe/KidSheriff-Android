package kr.co.starmark.kidsheriff.request;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
public class UserDataResult implements Parcelable {
	private String result;
	private String email;
	private int whichSide = 0;
    private List<String> linkedAccounts = new ArrayList<String>();

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getWhichSide() {
        return whichSide;
    }

    public void setWhichSide(int whichSide) {
        this.whichSide = whichSide;
    }

    public List<String> getLinkedAccounts() {
        return linkedAccounts;
    }

    public void setLinkedAccounts(List<String> linkedAccounts) {
        this.linkedAccounts = linkedAccounts;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.result);
        dest.writeString(this.email);
        dest.writeInt(this.whichSide);
        dest.writeList(this.linkedAccounts);
    }

    public UserDataResult() {
    }

    private UserDataResult(Parcel in) {
        this.result = in.readString();
        this.email = in.readString();
        this.whichSide = in.readInt();
        this.linkedAccounts = new ArrayList<String>();
        in.readList(this.linkedAccounts, null);
    }

    public static final Parcelable.Creator<UserDataResult> CREATOR = new Parcelable.Creator<UserDataResult>() {
        public UserDataResult createFromParcel(Parcel source) {
            return new UserDataResult(source);
        }

        public UserDataResult[] newArray(int size) {
            return new UserDataResult[size];
        }
    };

    @Override
    public String toString() {
        return "UserDataResult{" +
                "result='" + result + '\'' +
                ", email='" + email + '\'' +
                ", whichSide=" + whichSide +
                ", linkedAccounts=" + linkedAccounts +
                '}';
    }
}