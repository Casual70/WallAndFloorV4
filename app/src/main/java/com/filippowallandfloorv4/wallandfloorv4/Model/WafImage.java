package com.filippowallandfloorv4.wallandfloorv4.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by Filippo on 11/10/2015.
 */
public class WafImage implements Parcelable {

    public static final int TEXTURECODE_ON = 5;
    public static final int TEXTURECODE_OFF = 2;

    private File filePath;
    private String nomeProject;
    private String nomeZona;
    private int _id;
    private int textureCode;

    public WafImage (File filePath){
        this.filePath = filePath;
    }

    public WafImage (File filePath, String nomeProject, String nomeZona, int _id){
        this.filePath = filePath;
        this.nomeProject = nomeProject;
        this.nomeZona = nomeZona;
        this._id = _id;
        this.textureCode = TEXTURECODE_OFF;
    }

    public WafImage(File filePath, String nomeProject, String nomeZona, int _id, int textureCode) {
        this.filePath = filePath;
        this.nomeProject = nomeProject;
        this.nomeZona = nomeZona;
        this._id = _id;
        this.textureCode = textureCode;
    }

    public WafImage(){

    }
    private Bitmap prepareIcona(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap original = BitmapFactory.decodeFile(getFilePath().getAbsolutePath(),options);
        return  Bitmap.createScaledBitmap(original, 400, 300, true);
    }

    public void setFilePath(File filePath) {
        this.filePath = filePath;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public File getFilePath() {
        return filePath;
    }

    public Bitmap getIcona() {
        return prepareIcona();
    }

    public String getNomeProject() {
        return nomeProject;
    }

    public void setNomeProject(String nomeProject) {
        this.nomeProject = nomeProject;
    }

    public String getNomeZona() {
        return nomeZona;
    }

    public void setNomeZona(String nomeZona) {
        this.nomeZona = nomeZona;
    }

    public int getTextureCode() {
        return textureCode;
    }

    public void setTextureCode(int textureCode) {
        this.textureCode = textureCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.filePath);
        dest.writeString(this.nomeProject);
        dest.writeString(this.nomeZona);
        dest.writeInt(this.textureCode);
    }

    protected WafImage(Parcel in) {
        this.filePath = (File) in.readSerializable();
        this.nomeProject = in.readString();
        this.nomeZona = in.readString();
        this.textureCode = in.readInt();
    }

    public static final Creator<WafImage> CREATOR = new Creator<WafImage>() {
        public WafImage createFromParcel(Parcel source) {
            return new WafImage(source);
        }

        public WafImage[] newArray(int size) {
            return new WafImage[size];
        }
    };
}
