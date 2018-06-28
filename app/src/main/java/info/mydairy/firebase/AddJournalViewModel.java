package info.mydairy.firebase;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import info.mydairy.firebase.database.JournalDatabase;
import info.mydairy.firebase.database.JournalEntry;

/**
 * Created by hubert on 6/27/18.
 */

public class AddJournalViewModel extends ViewModel {
    private LiveData<JournalEntry> mJournalEntry;

    public AddJournalViewModel(JournalDatabase dB, int id){
        mJournalEntry = dB.journalDoa().loadAJournalEntryById(id);
    }

    public LiveData<JournalEntry> getJournal(){
        return mJournalEntry;
    }
}
