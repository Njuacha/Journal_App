package info.mydairy.firebase.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

/**
 * Created by hubert on 6/26/18.
 */
@Database(entities = {JournalEntry.class},version = 1,exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class JournalDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "journal";
    private static JournalDatabase sInstance;

    public static JournalDatabase getsInstance(Context context){
        if( sInstance == null){
            synchronized (LOCK){
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        JournalDatabase.class,JournalDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract JournalDoa journalDoa();

}
