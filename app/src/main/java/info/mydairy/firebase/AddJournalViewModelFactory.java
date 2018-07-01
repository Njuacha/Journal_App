package info.mydairy.firebase;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.util.Log;

import info.mydairy.firebase.database.JournalDatabase;

/**
 * Created by hubert on 6/27/18.
 */

class AddJournalViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final JournalDatabase mDb;
    private final int mJournalId;

    public AddJournalViewModelFactory(JournalDatabase dB,int journalId){
        mDb = dB;
        mJournalId = journalId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new AddJournalViewModel(mDb,mJournalId);
    }
}
