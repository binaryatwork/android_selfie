package course.lab.dailyselfie;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import course.lab.dailyselfie.SelfieContentProvider.DataContract;
import course.lab.dailyselfie.SelfieContentProvider.DatabaseOpenHelper;

public class SelfieViewAdapter extends BaseAdapter implements Observer{

	private ArrayList<SelfieRecord> list = new ArrayList<SelfieRecord>();
	private HashMap<String, SelfieRecord> map = new HashMap<String, SelfieRecord>();
	private static LayoutInflater inflater = null;
	private Context mContext;
	SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	DatabaseOpenHelper mDatabaseOpenHelper;
	ContentObserver mContentObserver;
	HashMap<String, ViewHolder> mViewHolders = new HashMap<String, ViewHolder>();


	public SelfieViewAdapter(Context context,
			DatabaseOpenHelper databaseOpenHelper) {
		super();
		mContext = context;
		inflater = LayoutInflater.from(mContext);
		mDatabaseOpenHelper = databaseOpenHelper;
		initialData();
		// observer the change of database
		mContentObserver = new ContentObserver(null) {
			public void onChange(boolean selfChange) {
				SelfieViewAdapter.this.update(null, null);
			}
		};
		context.getContentResolver().registerContentObserver(
				DataContract.CONTENT_URI, true, mContentObserver);
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View newView = convertView;
		ViewHolder holder;
		SelfieRecord curr = list.get(position);
		if (null == convertView) {
			holder = new ViewHolder();
			newView = inflater.inflate(R.layout.selfie_view, null);
			holder.pic = (ImageView) newView.findViewById(R.id.selfie_pic);
			holder.timestamp = (TextView) newView.findViewById(R.id.timestap);
			holder.checkbox = (CheckBox) newView.findViewById(R.id.checkbox);		
			holder.imageUri = curr.getPhotoUri();
			newView.setTag(holder);
		} else {
			holder = (ViewHolder) newView.getTag();
		}
		holder.pic.setImageBitmap(curr.getThumbnailBitmap());
		holder.checkbox.setChecked(false);
		holder.timestamp.setText("Time: "
				+ timeFormat.format(curr.getTimeStamp().getTime()));
		mViewHolders.put(curr.getPhotoUri(), holder);
		return newView;
	}

	static class ViewHolder {
		public CheckBox checkbox;
		ImageView pic;
		TextView timestamp;
		String imageUri;
	}

	/**
	 * add the path to database
	 * @param path
	 */
	public void add(String path) {
		mDatabaseOpenHelper.add(path);
	}
	/**
	 * delete the image from database
	 * @param path
	 */
	public void delete(String path) {
		mDatabaseOpenHelper.deleteSelfieRecords(path);
	}
	
	public void removeAll() {		
		mDatabaseOpenHelper.removeAll();
	}
	
	public ArrayList<SelfieRecord> getList() {
		return list;
	}

	/**
	 * when the app is first loaded, this method will load the selfie information from database
	 */
	private void initialData() {
		List<SelfieRecord> allSelfieRecords = mDatabaseOpenHelper
				.getAllSelfieRecord();
		for (SelfieRecord selfieRecord : allSelfieRecords) {
			addSelfieRecordFromDB(selfieRecord);
		}
		sendDataSetChangedNotification();
	}
	/**
	 * unregister to database changes
	 */
	public void onDestroy() {
		mContext.getContentResolver().unregisterContentObserver(
				mContentObserver);
	}

	/**
	 * call back method that it is invoked when there are any database changes
	 */
	@Override
	public void update(Observable observable, Object data) {
		List<String> allPaths = mDatabaseOpenHelper.getAllPaths();
		List<String> newRecords = new ArrayList<String>();
		for (String path : allPaths) {
			if (!map.containsKey(path)) {
				newRecords.add(path);
			}
		}
		List<SelfieRecord> selfieRecords = mDatabaseOpenHelper
				.getSelfieRecords(newRecords);
		
		for (SelfieRecord selfieRecord : selfieRecords) {
			addSelfieRecordFromDB(selfieRecord);
		}

		HashSet<String> keys = new HashSet<String>();
		keys.addAll(map.keySet());
		
		for (String photoUri : keys) {
			if (!allPaths.contains(photoUri)) {
				list.remove(map.get(photoUri));
				map.remove(photoUri);
				mViewHolders.remove(photoUri);
;				File imageFile = new File(photoUri);
				if (imageFile.exists()){
					imageFile.delete();
				}
			}
		}		
		sendDataSetChangedNotification();
	}
	/**
	 * after the list is chaged, notify all DataSetObserver, have to do it in UIThread since it
	 * may trigger UI update
	 */
	private void sendDataSetChangedNotification() {
		Handler mainHandler = new Handler(mContext.getMainLooper());
		Runnable myRunnable = new Runnable() {
			@Override
			public void run() {
				SelfieViewAdapter.this.notifyDataSetChanged();
			}
		};
		mainHandler.post(myRunnable);
	}
	/**
	 * add selfie from db
	 * @param selfieRecord
	 */
	private void addSelfieRecordFromDB(SelfieRecord selfieRecord) {
		selfieRecord.setThumbnailBitmap(ImageUtil.getBitmapThumbnail(
				selfieRecord.getPhotoUri(), mContext.getResources()));
		list.add(selfieRecord);
		map.put(selfieRecord.getPhotoUri(), selfieRecord);
	}

	/** 
	 * remove selected selfie records from database
	 */
	public void removeSelected() {
		List<String> deleted = new ArrayList<String>();
		for (Entry<String, ViewHolder> set: mViewHolders.entrySet()){
			if (set.getValue().checkbox.isChecked()){
				deleted.add(set.getKey());				
			}
		}
		for (String path:deleted){
			mDatabaseOpenHelper.deleteSelfieRecords(path);
		}
	}
}
