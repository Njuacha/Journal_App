package info.mydairy.firebase.database;

import java.util.Date;

/**
 * Created by hubert on 6/26/18.
 */

public class JournalSummaryEntry {
    int id;
    String textSummary;
    Date date;
    int feeling;

    public JournalSummaryEntry(int id, String textSummary, Date date, int feeling) {
        this.id = id;
        this.textSummary = textSummary;
        this.date = date;
        this.feeling = feeling;
    }

    public int getId() {
        return id;
    }

    public String getTextSummary() {
        return textSummary;
    }

    public Date getDate() {
        return date;
    }

    public int getFeeling() {
        return feeling;
    }
}
