package com.doit.todoo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Model implements Parcelable {
    private long id;
    private String title;
    private String content;
    private String color;
    private int isPasswordProtected;
    private String password;

    public Model(long id, String title, String content, String color, int isPasswordProtected, String password) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.color = color;
        this.isPasswordProtected = isPasswordProtected;
        this.password = password;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public int getIsPasswordProtected() {
        return isPasswordProtected;
    }

    public void setIsPasswordProtected(int isPasswordProtected) {
        this.isPasswordProtected = isPasswordProtected;
    }


    Model() {
    }

    public static final Creator<Model> CREATOR = new Creator<Model>() {
        @Override
        public Model createFromParcel(Parcel in) {
            Model title = new Model();
            title.id = in.readLong();
            title.title = in.readString();
            title.content = in.readString();
            title.color = in.readString();
            title.isPasswordProtected = in.readInt();
            title.password = in.readString();
            return title;
        }

        @Override
        public Model[] newArray(int size) {
            return new Model[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComment() {
        return title;
    }

    public void setComment(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(color);
        dest.writeInt(isPasswordProtected);
        dest.writeString(password);
    }
}