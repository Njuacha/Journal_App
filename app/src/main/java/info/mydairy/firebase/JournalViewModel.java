package info.mydairy.firebase;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import info.mydairy.firebase.database.JournalDatabase;
import info.mydairy.firebase.database.JournalSummaryEntry;

/**
 * View model class that loads all the journal summary entries from database
 */

class JournalViewModel extends AndroidViewModel {
    private LiveData<List<JournalSummaryEntry>> mJournalSummaryEntries;

    public JournalViewModel(@NonNull Application application) {
        super(application);

        JournalDatabase dB = JournalDatabase.getsInstance(this.getApplication());
        mJournalSummaryEntries = dB.journalDoa().loadAllJournalSummaryEntries();
    }

    public LiveData<List<JournalSummaryEntry>> getmJournalSummaryEntries(){
        return  mJournalSummaryEntries;
    }

}
