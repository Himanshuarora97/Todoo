package com.doit.todoo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Model implements Parcelable {
    private long id;
    private String comment;
    private String content;
    private String color;
    private int isPasswordProtected;
    private String password;

    public Model(long id, String comment, String content, String color, int isPasswordProtected, String password) {
        this.id = id;
        this.comment = comment;
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
            Model comment = new Model();
            comment.id = in.readLong();
            comment.comment = in.readString();
            comment.content = in.readString();
            comment.color = in.readString();
            comment.isPasswordProtected = in.readInt();
            comment.password = in.readString();
            return comment;
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
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return comment;
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
        dest.writeString(comment);
        dest.writeString(content);
        dest.writeString(color);
        dest.writeInt(isPasswordProtected);
        dest.writeString(password);
    }
}