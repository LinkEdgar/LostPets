import android.provider.BaseColumns;
import android.provider.SyncStateContract;

/**
 * Created by EndUser on 10/22/2017.
 */

public class PetDatabaseContract {
    // we do not need an instance of this class
    private PetDatabaseContract(){}

    public static abstract class PetEntry implements BaseColumns{
        //table name
        public static final String TABLE_NAME = "pets";

        //column names
        public static final String _ID = SyncStateContract.Columns._ID;
        public static final String COLUMN_PET_NAME = "name";
        public static final String COLUMN_PET_BREED = "breed";
        public static final String COLUMN_PET_WEIGHT = "weight";
        public static final String COLUMN_PET_GENDER = "gender";

        //values for gender
        public static int GENDER_UNKNOWN = 0;
        public static int GENDER_MALE = 1;
        public static int GENDER_FEMAKE = 2;

    }

}
