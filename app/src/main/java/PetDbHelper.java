import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by EndUser on 10/22/2017.
 */

public class PetDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "lost.db";
    private static final int DATABASE_VERSION = 1;
    //create table sql statements
    private static final String SQL_CREATE_PET_TABLE = "CREATE"+ PetDatabaseContract.PetEntry.TABLE_NAME +"("
            + PetDatabaseContract.PetEntry._ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PetDatabaseContract.PetEntry.COLUMN_PET_NAME + "TEXT NOT NULL,"
            + PetDatabaseContract.PetEntry.COLUMN_PET_BREED + "TEXT,"
            + PetDatabaseContract.PetEntry.COLUMN_PET_GENDER + "INTEGER NOT NULL,"
            + PetDatabaseContract.PetEntry.COLUMN_PET_WEIGHT + "INTEGER NOT NULL DEFAULT 0);";

    //TODO check if the constructor needs to be private
    public PetDbHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PET_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Nothing for now but eventually we'll add the code to drop the old database and make a new one
        //and updating the version number
    }
}
