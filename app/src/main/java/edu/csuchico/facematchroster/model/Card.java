package edu.csuchico.facematchroster.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by sobral on 4/25/15.
 */

@Table(name = "Cards")
public class Card extends Model {

    @Column(name = "photo")
    private String mPhoto; // front card

    @Column(name = "name")
    private String mName; // back card

    // the current e-factor for this card
    @Column(name = "efactor")
    private float mEFactor = 2.5f;

    @Column(name = "creation_date")
    private Date mCreationDate;

    @Column(name = "update_date")
    private Date mUpdateDate;

    public Card() {
    }

    public Card(String mPhoto, String mName, float mEFactor, Date mCreationDate, Date mUpdateDate) {
        this.mPhoto = mPhoto;
        this.mName = mName;
        this.mEFactor = mEFactor;
        this.mCreationDate = mCreationDate;
        this.mUpdateDate = mUpdateDate;
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
