package info.mydairy.firebase;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import info.mydairy.firebase.database.JournalEntry;
import info.mydairy.firebase.database.JournalSummaryEntry;

/**
 * This JournalAdapter creates and binds ViewHolders, that hold the textSummary and feeling of a journalEntry,
 * to a RecyclerView to efficiently display data.
 */

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {
    // Constant for date format
    private static final String DATE_FORMAT = "dd/MM/yyy";
//TODO 3: Change DATE_FORMAT to something that results to a date like "1a.m Friday 01/02/1996"

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;
    // Class variables for the List that holds task data and the Context
    private List<JournalSummaryEntry> mJournalSummaryEntries;
    private Context mContext;
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    /**
     * Constructor for the TaskAdapter that initializes the Context and itemClickListener.
     *
     * @param context  the current Context
     * @param itemClickListener the ItemClickListener
     */
    public JournalAdapter(Context context, ItemClickListener itemClickListener) {
        mContext = context;
        mItemClickListener = itemClickListener;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new JournalViewHolder that holds the view for each journalEntry
     */
    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.journal_layout, parent, false);
        return new JournalViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        // Determine the values of the wanted data
        JournalSummaryEntry journalSummaryEntry = mJournalSummaryEntries.get(position);
        String textSummary = journalSummaryEntry.getTextSummary();
        int feeling = journalSummaryEntry.getFeeling();
        String createdAt = dateFormat.format(journalSummaryEntry.getDate());

        // Set values
        holder.textSummaryView.setText(textSummary.concat("..."));
        holder.dateCreatedView.setText(createdAt);

        // Programmatically set color of feelingCircle in item view
        int feelingColor = getFeelingColor(feeling);
        GradientDrawable feelingCircle = (GradientDrawable) holder.feelingView.getBackground();
        feelingCircle.setColor(feelingColor);
    }

    /*
    Helper method for selecting the correct feeling circle color.
    P1 = HappyColor , P2 = NormalColor, P3 = SadColor
    */
    private int getFeelingColor(int feeling) {
        int feelingColor = 0;
        switch (feeling){
            case 1:
                feelingColor = ContextCompat.getColor(mContext,R.color.colorJoyful);
                break;
            case 2:
                feelingColor = ContextCompat.getColor(mContext,R.color.colorNormal);
                break;
            case 3:
                feelingColor = ContextCompat.getColor(mContext,R.color.colorSad);
                break;
            default:
                break;
        }
        return feelingColor;
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mJournalSummaryEntries == null){
            return 0;
        }
        return mJournalSummaryEntries.size();
    }

    public List<JournalSummaryEntry> getmJournalSummaryEntries() {
        return mJournalSummaryEntries;
    }

    /**
     * When data changes, this method updates the list of JournalSummaryEntries
     * and notifies the adapter to use the new values on it
     */
    public void setJournalEntries(List<JournalSummaryEntry> journalSummaryEntries){
        mJournalSummaryEntries = journalSummaryEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    // Inner class for creating ViewHolders
    public class JournalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textSummaryView;
        TextView dateCreatedView;
        View feelingView;

        /**
         * Constructor for the JournalViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public JournalViewHolder(View itemView) {
            super(itemView);

            textSummaryView = itemView.findViewById(R.id.text_view_summary);
            dateCreatedView = itemView.findViewById(R.id.text_view_date_created);
            feelingView = itemView.findViewById(R.id.view_feeling);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int itemId = mJournalSummaryEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(itemId);
        }
    }
}
