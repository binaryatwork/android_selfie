package course.lab.dailyselfie.SelfieContentProvider;

import android.content.ContentResolver;
import android.net.Uri;

// Contract Class for accessing ContentResolver 

public final class DataContract {

	public static final String AUTHORITY = "course.labs.selfielab.provider";
	public static final Uri BASE_URI = Uri
			.parse("content://" + AUTHORITY + "/");

	public final static String TABLE_NAME = "selfie_info";


	public final static String DB_NAME = "selfie_info_db";
	public final static Integer VERSION = 1;

	// The URI for this table.
	public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI,
			TABLE_NAME);

	public final static String KEY_PHOTO_PATH = "photo_path";
	public final static String KEY_TIMESTAMP = "timestamp";
	public final static String _ID = "_id";
	

	// Mime type for a directory of data items
	public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/StringContentProvider.data.text";

	// Mime type for a single data item
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/StringContentProvider.data.text";

	// All columns of this table
	public static final String[] ALL_COLUMNS = { _ID, KEY_PHOTO_PATH, KEY_TIMESTAMP };

}