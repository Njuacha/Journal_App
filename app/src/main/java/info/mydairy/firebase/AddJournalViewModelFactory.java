package info.mydairy.firebase;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.util.Log;

import info.mydairy.firebase.database.JournalDatabase;

/**
 * Created by hubert on 6/27/18.
 */

public class AddJournalViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final JournalDatabase mDb;
    private final int mJournalId;

    public AddJournalViewModelFactory(JournalDatabase dB,int journalId){
        mDb = dB;
        mJournalId = journalId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass){
        return (T) new AddJournalViewModel(mDb,mJournalId);
    }
}
