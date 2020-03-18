package me.nereo.multi_image_selector.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @Author zhangyabo
 * @Date 2019-12-23
 * @Des
 **/
public class SelectBean implements Parcelable {
    String path;
    int position;
    boolean isAdd = false;

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    protected SelectBean(Parcel in) {
        path = in.readString();
        position = in.readInt();
    }

    public SelectBean(String path, int position) {
        this.path = path;
        this.position = position;
    }

    public static final Creator<SelectBean> CREATOR = new Creator<SelectBean>() {
        @Override
        public SelectBean createFromParcel(Parcel in) {
            return new SelectBean(in);
        }

        @Override
        public SelectBean[] newArray(int size) {
            return new SelectBean[size];
        }
    };

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeInt(position);
    }
}
