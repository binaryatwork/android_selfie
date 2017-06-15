package course.lab.dailyselfie.SelfieContentProvider;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import course.lab.dailyselfie.SelfieRecord;

/**
 * database helper to encapsulate database logic
 * @author rchan
 *
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

	final private static String CREATE_CMD =

	"CREATE TABLE " + DataContract.TABLE_NAME + " (" + DataContract._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ DataContract.KEY_PHOTO_PATH + " TEXT NOT NULL, "
			+ DataContract.KEY_TIMESTAMP
			+ " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + ")";

	final private Context mContext;

	public DatabaseOpenHelper(Context context) {
		super(context, DataContract.DB_NAME, null, DataContract.VERSION);
		this.mContext = context;
	}
	/**
	 * get all image paths from database
	 * @return
	 */
	public List<String> getAllPaths() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("select " + DataContract.KEY_PHOTO_PATH
				+ " from " + DataContract.TABLE_NAME, null);
		List<String> paths = new ArrayList<String>();
		if (cursor.moveToFirst()) {
			do {
				paths.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		return paths;
	}

	/**
	 * create db if it does not exist
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CMD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// N/A
	}
	
	/**
	 * delete database
	 */
	void deleteDatabase() {
		mContext.deleteDatabase(DataContract.DB_NAME);
	}

	/**
	 * add new image information
	 * @param path
	 */
	public void add(String path) {
		// Gets the data repository in write mode
		SQLiteDatabase db = getWritableDatabase();
		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(DataContract.KEY_PHOTO_PATH, path);
		getReadableDatabase().insert(DataContract.TABLE_NAME, null, values);
		mContext.getContentResolver().notifyChange(DataContract.CONTENT_URI,
				null);
	}

	/**
	 * get all selfie records
	 * @return
	 */
	public List<SelfieRecord> getAllSelfieRecord() {
		SQLiteDatabase db = getReadableDatabase();
		String sortOrder = DataContract.KEY_TIMESTAMP + " DESC";
		Cursor cursor = db.query(DataContract.TABLE_NAME,
				DataContract.ALL_COLUMNS, null, null, null, null, sortOrder);
		return getSelfieRecordsFromCursor(cursor);
	}

	/**
	 * remove all selfie records
	 */
	public void removeAll() {
		if (0<getWritableDatabase().delete(DataContract.TABLE_NAME, null, null)){
			mContext.getContentResolver().notifyChange(DataContract.CONTENT_URI,null);
		}
	}

	/**
	 * get selfie records
	 * @param paths
	 * @return
	 */
	public List<SelfieRecord> getSelfieRecords(List<String> paths) {
		List<SelfieRecord> selfieRecords = new ArrayList<SelfieRecord>();
		for (String path : paths) { //
			Cursor cursor = getReadableDatabase().query(
					DataContract.TABLE_NAME, DataContract.ALL_COLUMNS,
					DataContract.KEY_PHOTO_PATH + "=?", new String[] { path },
					null, null, null);
			if (cursor.moveToFirst()) {
				selfieRecords.add(getSelfieRecordFromCursor(cursor));
			}
		}
		return selfieRecords;
	}

	/** 
	 * delete selfie records
	 * @param path
	 */
	public void deleteSelfieRecords(String path) {
		if (0<getWritableDatabase().delete(DataContract.TABLE_NAME,
				DataContract.KEY_PHOTO_PATH + "=?", new String[] { path })){
			mContext.getContentResolver().notifyChange(DataContract.CONTENT_URI,null);
		}	
	}

	/** 
	 * loop through the cursor to create selfie records
	 * @param cursor
	 * @return
	 */
	private List<SelfieRecord> getSelfieRecordsFromCursor(Cursor cursor) {
		List<SelfieRecord> selfieRecords = new ArrayList<SelfieRecord>();
		if (cursor.moveToFirst()) {
			do {
				selfieRecords.add(getSelfieRecordFromCursor(cursor));
			} while (cursor.moveToNext());

		}
		return selfieRecords;
	}

	private SelfieRecord getSelfieRecordFromCursor(Cursor cursor) {
		String path = cursor.getString(cursor
				.getColumnIndex(DataContract.KEY_PHOTO_PATH));
		Calendar timestamp = Calendar.getInstance();
		timestamp
				.setTimeInMillis(Timestamp.valueOf(
						cursor.getString(cursor
								.getColumnIndex(DataContract.KEY_TIMESTAMP)))
						.getTime());
		SelfieRecord selfieRecord = new SelfieRecord(timestamp, path);

		return selfieRecord;
	}
}
