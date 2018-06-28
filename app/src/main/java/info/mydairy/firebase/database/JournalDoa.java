package info.mydairy.firebase.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by hubert on 6/26/18.
 */
@Dao
public interface JournalDoa {

    // loads all columns of database except the text. This saves
    // time and mermory as it loads only data needed
    @Query("SELECT id,textSummary,date,feeling FROM JournalEntry")
    LiveData<List<JournalSummaryEntry>> loadAllJournalSummaryEntries();

    @Insert
    void insertAJournalEntry(JournalEntry journalEntry);

    @Update
    void updateJournalEntry(JournalEntry journalEntry);

    @Delete
    void deleteJournalEntry(JournalEntry journalEntry);

    @Query("SELECT * FROM JournalEntry WHERE id = :id")
    LiveData<JournalEntry> loadAJournalEntryById(int id);
}
