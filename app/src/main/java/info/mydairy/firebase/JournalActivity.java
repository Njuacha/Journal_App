package info.mydairy.firebase;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.List;

import info.mydairy.firebase.database.JournalSummaryEntry;

public class JournalActivity extends AppCompatActivity implements JournalAdapter.ItemClickListener{

    // Member variables for recycler view and adapter
    private RecyclerView mRecyclerView;
    private JournalAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JournalActivity.this,AddJournalActivity.class);
                startActivity(intent);
            }
        });

        // Set the recyclerView to the corresponding view
        mRecyclerView = findViewById(R.id.recycler_view_journal_entries);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // initialize the adapter and attatch it to the recyclerView
        mAdapter = new JournalAdapter(this,this);
        mRecyclerView.setAdapter(mAdapter);

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
}
