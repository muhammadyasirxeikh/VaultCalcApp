package com.codecoy.securecalculator.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Model implements Parcelable {
    public static final Creator<Model> CREATOR = new C06171();
    private int Id;
    private long LastModified;
    private String Path;
    private boolean isEditable = false;
    private boolean isSelected = false;
    private String newPath;

    public Model() {

    }

    static class C06171 implements Creator<Model> {
        C06171() {
        }

        public Model createFromParcel(Parcel source) {
            return new Model(source);
        }

        public Model[] newArray(int size) {
            return new Model[size];
        }
    }

    public boolean isEditable() {
        return this.isEditable;
    }

    public void setEditable(boolean editable) {
        this.isEditable = editable;
    }

    public Model(String Path, long LastModified) {
        this.Path = Path;
        this.LastModified = LastModified;
    }

    public Model(String Path, String newPath) {
        this.Path = Path;
        this.newPath = newPath;
    }

    public Model(int Id, String Path, String newPath) {
        this.Id = Id;
        this.Path = Path;
        this.newPath = newPath;
    }

    public int getId() {
        return this.Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getNewPath() {
        return this.newPath;
    }

    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }

    public static Creator<Model> getCREATOR() {
        return CREATOR;
    }

    public long getLastModified() {
        return this.LastModified;
    }

    public void setLastModified(long LastModified) {
        this.LastModified = LastModified;
    }

    public String getPath() {
        return this.Path;
    }

    public void setPath(String Path) {
        this.Path = Path;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        byte b;
        byte b2 = (byte) 1;
        dest.writeInt(this.Id);
        dest.writeString(this.Path);
        dest.writeString(this.newPath);
        if (this.isSelected) {
            b = (byte) 1;
        } else {
            b = (byte) 0;
        }
        dest.writeByte(b);
        if (!this.isEditable) {
            b2 = (byte) 0;
        }
        dest.writeByte(b2);
        dest.writeLong(this.LastModified);
    }

    protected Model(Parcel in) {
        boolean z;
        boolean z2 = true;
        this.Id = in.readInt();
        this.Path = in.readString();
        this.newPath = in.readString();
        if (in.readByte() != (byte) 0) {
            z = true;
        } else {
            z = false;
        }
        this.isSelected = z;
        if (in.readByte() == (byte) 0) {
            z2 = false;
        }
        this.isEditable = z2;
        this.LastModified = in.readLong();
    }
}
