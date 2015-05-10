package edu.csuchico.facematchroster.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Pedro Henrique on 4/25/15 - 7:51 PM.
 */

@Table(name = "Decks")
public class Deck extends Model {

    @Column(name = "tile")
    private String mTitle;

    @Column(name = "class_id")
    private String classId;

    @Column(name = "creation_date")
    private Date mCreationDate;

    @Column(name = "update_date")
    private Date mUpdateDate;

    // This method is optional, does not affect the foreign key creation.
    public List<Card> cards() {
        return getMany(Card.class, "Deck");
    }

    public Deck() {
    }

    public Deck(String mTitle, String classId, Date mCreationDate, Date mUpdateDate) {
        this.mTitle = mTitle;
        this.classId = classId;
        this.mCreationDate = mCreationDate;
        this.mUpdateDate = mUpdateDate;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
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
