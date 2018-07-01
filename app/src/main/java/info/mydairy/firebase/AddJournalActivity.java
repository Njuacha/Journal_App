package info.mydairy.firebase;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Date;

import info.mydairy.firebase.database.JournalDatabase;
import info.mydairy.firebase.database.JournalEntry;

public class AddJournalActivity extends AppCompatActivity {
    // Extra for the journal ID to be received in the intent
    public static final String EXTRA_JOURNAL_ID = "extraJournalId";
    // Constant for default journal id to be used when not in update mode
    private static final int DEFAULT_JOURNAL_ID = -1;
    // Extra for the journal ID to be received after rotation
    private static final String INSTANCE_JOURNAL_ID = "instanceJournalId";


    private static final int FEELING_HAPPY = 1;
    private static final int FEELING_NORMAL = 2;
    private static final int FEELING_SAD = 3;
    private static final String EXTRA_READ_MODE = "readModeExtra";
    private static final String EXTRA_FEELING = "feelingExtra";
    private static final String EXTRA_JTEXT = "journalTextExtra";
    // fields to obtain text and feeling respectively
    private EditText mEditText;
    private TextView mTextView;
    private RadioGroup mRadioGroup;


    private int mJournalId = DEFAULT_JOURNAL_ID;
    private Menu mMenu;
    private JournalDatabase mDb;
    private boolean mReadMode;
    private FloatingActionButton mFab;
    private int mFeeling;
    private String mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();

        // Set the action bar back button to look like an up button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initViews();

        // Get all info relevant info that was saved in a previous instance such as journalId and journalText
        collectAllIfAnySavedInstanceStates(savedInstanceState);

        // Instantiate the database object
        mDb = JournalDatabase.getsInstance(getApplicationContext());

        // Gets data from addJournalview model and populates the UI
        setUpDataInViews();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_JOURNAL_ID, mJournalId);
        outState.putBoolean(EXTRA_READ_MODE, mReadMode);
        outState.putInt(EXTRA_FEELING, mFeeling);
        outState.putString(EXTRA_JTEXT, mText);
        super.onSaveInstanceState(outState);
    }

    // Inflates the add journal menu and sets readmode or editmode
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("Tag", "i have been intialized");
        mMenu = menu;
        getMenuInflater().inflate(R.menu.activity_add_journal, menu);

        if (mReadMode) {
            setReadMode();
        } else setEditMode();

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
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void setUpDataInViews() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_JOURNAL_ID)) {

            //11: Completed 11: Made layout of feeling to disappear so that it appears when the pencil is touched. The text should be unEditable and background should appear as the feeling color until when pencil is touched
            if (mJournalId == DEFAULT_JOURNAL_ID) {
                mJournalId = intent.getIntExtra(EXTRA_JOURNAL_ID, DEFAULT_JOURNAL_ID);
                // Create a viewmodel for the the journalEntry so that it can survive configuration changes
                AddJournalViewModelFactory factory = new AddJournalViewModelFactory(mDb, mJournalId);
                final AddJournalViewModel viewModel = ViewModelProviders.of(this, factory).get(AddJournalViewModel.class);

                viewModel.getJournal().observe(this, new Observer<JournalEntry>() {
                    @Override
                    public void onChanged(@Nullable JournalEntry journalEntry) {
                        viewModel.getJournal().removeObserver(this);
                        populateUI(journalEntry);
                        mReadMode = true;
                    }
                });
            }
        }
    }

    // refereneces all the views in layout
    private void initViews() {
        mEditText = findViewById(R.id.editText);
        mTextView = findViewById(R.id.textView);
        mRadioGroup = findViewById(R.id.radioGroup);

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Makes feelingLayout to be visible
                setEditMode();
            }
        });
    }

    private void collectAllIfAnySavedInstanceStates(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_JOURNAL_ID)
                && savedInstanceState.containsKey(EXTRA_READ_MODE)
                && savedInstanceState.containsKey(EXTRA_FEELING)) {
            mJournalId = savedInstanceState.getInt(INSTANCE_JOURNAL_ID);
            mReadMode = savedInstanceState.getBoolean(EXTRA_READ_MODE);
            mFeeling = savedInstanceState.getInt(EXTRA_FEELING);
            mText = savedInstanceState.getString(EXTRA_JTEXT);
            // Sets the text from previous instance
            mTextView.setText(mText);
        }
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
                if (mJournalId == DEFAULT_JOURNAL_ID) {
                    mDb.journalDoa().insertAJournalEntry(journalEntry);
                } else {
                    journalEntry.setId(mJournalId);
                    mDb.journalDoa().updateJournalEntry(journalEntry);
                }
                finish();
            }
        });

    }

    // Extracts the first three words from the text
    private String extractTextSummary(String text) {
        text = text.trim();
        int count = 3;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ' ') {
                count--;
            }
            if (count == 0) {
                return text.substring(0, i);
            }
        }
        return text;
        // Completed 10: Improved on this method so it can handle diverse cases
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

    // Sets the text and feeling from db into textview and sets feeling variables respectivly
    private void populateUI(JournalEntry journalEntry) {
        if (journalEntry == null) {
            return;
        }
        mText = journalEntry.getText();
        mTextView.setText(mText);
        mFeeling = journalEntry.getFeeling();
    }

    // Sets the background of the editText to a light color that represents the feeling
    private void setFeelingInBackground(int feeling) {
        switch (feeling) {
            case FEELING_HAPPY:
                mTextView.setBackgroundColor(getResources().getColor(R.color.colorJoyfulLight));
                mRadioGroup.check(R.id.radButtonHappy);
                break;
            case FEELING_NORMAL:
                mTextView.setBackgroundColor(getResources().getColor(R.color.colorNormalLight));
                mRadioGroup.check(R.id.radButtonNormal);
                break;
            case FEELING_SAD:
                mTextView.setBackgroundColor(getResources().getColor(R.color.colorSadLight));
                mRadioGroup.check(R.id.radButtonSad);
        }
    }

    // Makes the save and cancel options visible or invisible
    private void setEditMenuGroupVisible(boolean value) {
        mMenu.setGroupVisible(0, value);
    }

    // Sets all needed views for read mode to visible and those not needed to invisible.
    private void setReadMode() {
        setEditMenuGroupVisible(false);
        findViewById(R.id.layout_feeling).setVisibility(View.GONE);
        mFab.setVisibility(View.VISIBLE);
        setFeelingInBackground(mFeeling);
        // Remove the editText and make the textview visible
        mEditText.setVisibility(View.GONE);
        mTextView.setVisibility(View.VISIBLE);
    }

    // Does the oposite of setReadMode()
    private void setEditMode() {
        mReadMode = false;
        setEditMenuGroupVisible(true);
        findViewById(R.id.layout_feeling).setVisibility(View.VISIBLE);
        mFab.setVisibility(View.GONE);
        // Copy text from textview and paste on edittext then make textview disappear
        mEditText.setVisibility(View.VISIBLE);
        mEditText.setText(mTextView.getText());
        mTextView.setVisibility(View.GONE);
    }

}
