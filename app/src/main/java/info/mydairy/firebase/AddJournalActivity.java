package info.mydairy.firebase;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.Date;

import info.mydairy.firebase.database.JournalDatabase;
import info.mydairy.firebase.database.JournalEntry;

public class AddJournalActivity extends AppCompatActivity {
    // Extra for the journal ID to be received in the intent
    public static final String EXTRA_JOURNAL_ID = "extraTaskId";
    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_JOURNAL_ID = -1;



    public static final int FEELING_HAPPY = 1;
    public static final int FEELING_NORMAL = 2;
    public static final int FEELING_SAD = 3;
    // fields to obtain text and feeling respectively
    EditText mEditText;
    RadioGroup mRadioGroup;


    private int mJournalId = DEFAULT_JOURNAL_ID;

    private JournalDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEditText = findViewById(R.id.editText);
        mRadioGroup = findViewById(R.id.radioGroup);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Makes feelingLayout to be visible
                findViewById(R.id.layout_feeling).setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDb = JournalDatabase.getsInstance(getApplicationContext());

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_JOURNAL_ID)) {
            fab.setVisibility(View.VISIBLE);

            //TODO 11: Make layout of feeling to disappear so that it appears when the pencil is touched. The text should be unEditable and background should appear as the feeling color until when pencil is touched
            if (mJournalId == DEFAULT_JOURNAL_ID) {
                mJournalId = intent.getIntExtra(EXTRA_JOURNAL_ID, DEFAULT_JOURNAL_ID);
                // Create a viewmodel for the the journalEntry so that it can survive configuration changes
                AddJournalViewModelFactory factory = new AddJournalViewModelFactory(mDb, mJournalId);
                final AddJournalViewModel viewModel = ViewModelProviders.of(this,factory).get(AddJournalViewModel.class);

                viewModel.getJournal().observe(this, new Observer<JournalEntry>() {
                    @Override
                    public void onChanged(@Nullable JournalEntry journalEntry) {
                        viewModel.getJournal().removeObserver(this);
                        populateUI(journalEntry);
                    }
                });
            }
        }

    }


    // Inflates the add journal menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_journal, menu);
        return true;
    }

    // Carries out the appropriate action base on the menu item clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.menu_save:
                // adds new journal entry and closses this activity
                save();
                break;
            case R.id.menu_cancel:
                // returns to the Journal activity discarding any changes
                // close();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    // Creates a new journal entry and adds to the database
    private void save() {
        // Get all data to create a journal entry
        String text = mEditText.getText().toString();
        int feeling = getFeelingFromView();
        Date date = new Date();
        // extracts the first three words from the text
        String textSummary = extractTextSummary(text);
        // Create a new journalEntry
        final JournalEntry journalEntry = new JournalEntry(text, date, textSummary, feeling);
        // Add insert the entry into the database using a thread
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if( mJournalId == DEFAULT_JOURNAL_ID){
                    mDb.journalDoa().insertAJournalEntry(journalEntry);
                }else{
                    journalEntry.setId(mJournalId);
                    mDb.journalDoa().updateJournalEntry(journalEntry);
                }
                finish();
            }
        });

    }

    // Extracts the first three words from the text
    private String extractTextSummary(String text) {
        int count = 3;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ' ') {
                count--;
            }
            if (count == 0) {
                return text.substring(0, i);
            }
        }
        return text + "...";
        // TODO 10: Improve on this method so it can handle diverse cases
    }

    private int getFeelingFromView() {
        int feeling = 1;
        int checkedId = mRadioGroup.getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.radButtonHappy:
                feeling = FEELING_HAPPY;
                break;
            case R.id.radButtonNormal:
                feeling = FEELING_NORMAL;
                break;
            case R.id.radButtonSad:
                feeling = FEELING_SAD;
                break;
        }
        return feeling;
    }

    private void populateUI(JournalEntry journalEntry) {
        if (journalEntry == null) {
            return;
        }

        mEditText.setText(journalEntry.getText());
        int feeling = journalEntry.getFeeling();
        setFeelingInBackground(feeling);
        // Makes the feelingView to disappear as it is not need until when user clicks on fab
        findViewById(R.id.layout_feeling).setVisibility(View.GONE);
    }

    // Sets the background of the editText to a light color that represents the feeling
    private void setFeelingInBackground(int feeling) {
        switch(feeling){
            case FEELING_HAPPY:
                mEditText.setBackgroundColor(getResources().getColor(R.color.colorJoyfulLight));
                break;
            case FEELING_NORMAL:
                mEditText.setBackgroundColor(getResources().getColor(R.color.colorNormalLight));
                break;
            case FEELING_SAD:
                mEditText.setBackgroundColor(getResources().getColor(R.color.colorSadLight));
        }
    }
}
