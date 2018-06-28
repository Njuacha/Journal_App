package info.mydairy.firebase.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by hubert on 6/26/18.
 */


@Entity
public class JournalEntry {

    @PrimaryKey(autoGenerate = true)
    int id;
    String text;
    String textSummary;
    Date date;
    // Represents the feeling that the writter had when writting text
    int feeling;

    public JournalEntry(int id, String text, Date date, String textSummary, int feeling) {
        this.id = id;
        this.text = text;
        this.date = date;
        this.textSummary = textSummary;
        this.feeling = feeling;
    }

    @Ignore
    public JournalEntry(String text, Date date, String textSummary, int feeling) {
        this.text = text;
        this.date = date;
        this.textSummary = textSummary;
        this.feeling = feeling;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTextSummary() {
        return textSummary;
    }

    public void setTextSummary(String textSummary) {
        this.textSummary = textSummary;
    }

    public int getFeeling() {
        return feeling;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }
}
