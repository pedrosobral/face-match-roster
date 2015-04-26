package edu.csuchico.facematchroster.model;

import java.util.Date;

/**
 * Created by sobral on 4/25/15.
 */
public class Card {

    private int mId;

    private String mPhoto; // front card

    private String mName; // back card

    // the current e-factor for this card
    private float mEFactor = 2.5f;

    private Date mCreationDate;

    private Date mUpdateDate;

    public Card(String mPhoto, String mName, float mEFactor, Date mCreationDate, Date mUpdateDate) {
        this.mPhoto = mPhoto;
        this.mName = mName;
        this.mEFactor = mEFactor;
        this.mCreationDate = mCreationDate;
        this.mUpdateDate = mUpdateDate;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String mPhoto) {
        this.mPhoto = mPhoto;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public float getEFactor() {
        return mEFactor;
    }

    public void setEFactor(float mEFactor) {
        this.mEFactor = mEFactor;
    }

    public Date getCreationDate() {
        return mCreationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.mCreationDate = creationDate;
    }

    public Date getUpdateDate() {
        return mUpdateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.mUpdateDate = updateDate;
    }
}
