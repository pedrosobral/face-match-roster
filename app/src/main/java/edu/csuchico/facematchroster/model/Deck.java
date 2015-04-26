package edu.csuchico.facematchroster.model;

import java.util.Date;

import java.util.ArrayList;

/**
 * Created by Pedro Henrique on 4/25/15 - 7:51 PM.
 */
public class Deck {

    private int mId;

    private String mTitle;

    private ArrayList<Card> mDeck;

    private Date mCreationDate;

    private Date mUpdateDate;

    public Deck(int mId, String mTitle, ArrayList<Card> mDeck, Date mCreationDate, Date mUpdateDate) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mDeck = mDeck;
        this.mCreationDate = mCreationDate;
        this.mUpdateDate = mUpdateDate;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public ArrayList<Card> getDeck() {
        return mDeck;
    }

    public void setDeck(ArrayList<Card> mDeck) {
        this.mDeck = mDeck;
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
