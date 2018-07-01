package info.mydairy.firebase;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import info.mydairy.firebase.database.JournalDatabase;
import info.mydairy.firebase.database.JournalEntry;
import info.mydairy.firebase.database.JournalSummaryEntry;

public class JournalActivity extends AppCompatActivity implements JournalAdapter.ItemClickListener{

    // An extra to be used for signing out
    public static String EXTRA_SIGN_OUT = "sign out";
    private JournalAdapter mAdapter;
    private JournalDatabase mdB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(this, SignIn.class));
            finish();
        }
        setContentView(R.layout.ativity_journal);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JournalActivity.this,AddJournalActivity.class);
                startActivity(intent);
            }
        });

        // Set the recyclerView to the corresponding view
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view_journal_entries);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // initialize the adapter and attatch it to the recyclerView
        mAdapter = new JournalAdapter(this,this);
        mRecyclerView.setAdapter(mAdapter);

        // implements the swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // Get the position of item selected
                        int position = viewHolder.getAdapterPosition();
                        // Get the list of current items in adapter
                        List<JournalSummaryEntry> jSummaryEntries = mAdapter.getmJournalSummaryEntries();
                        // Use the position of item to get item selected from list
                        JournalSummaryEntry jSummaryEntry = jSummaryEntries.get(position);
                        // Get the item's id and use it to delete the item in the database
                        mdB.journalDoa().deleteJournalEntryWithId(jSummaryEntry.getId());
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);

        mdB = JournalDatabase.getsInstance(getApplicationContext());
        setUpViewModel();
    }

    @Override
    public void onItemClickListener(int itemId) {
        // Launch AddJActivity adding the itemId as an extra in the intent
        Intent viewJournalEntryIntent = new Intent(JournalActivity.this, AddJournalActivity.class);
        viewJournalEntryIntent.putExtra(AddJournalActivity.EXTRA_JOURNAL_ID,itemId);
        startActivity(viewJournalEntryIntent);
    }
    // Sets Up JournalViewModel which loads journal summary entries.
    // creates an observer for the live data returned
    private void setUpViewModel(){
        JournalViewModel viewModel = ViewModelProviders.of(this).get(JournalViewModel.class);
        viewModel.getmJournalSummaryEntries().observe(this, new Observer<List<JournalSummaryEntry>>() {
            @Override
            public void onChanged(@Nullable List<JournalSummaryEntry> journalSummaryEntries) {
                mAdapter.setJournalEntries(journalSummaryEntries);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, "Sign Out");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            signOut();
        }
        return true;
    }

    private void signOut() {
        Intent signOutIntent = new Intent(JournalActivity.this, SignIn.class);
        signOutIntent.putExtra(EXTRA_SIGN_OUT, true);
        startActivity(signOutIntent);
        finish();
    }
}
