package course.lab.dailyselfie;

import java.io.File;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import course.lab.dailyselfie.SelfieContentProvider.DatabaseOpenHelper;

public class SelfieViewListActivity extends ListActivity {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private String  mCurrentImagePath;
	private SelfieViewAdapter mAdapter;
	protected static final String EXTRA_RES_ID = "POS";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create a new Adapter containing a list of colors
		// Set the adapter on this ListActivity's built-in ListView
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item));
		ListView lv = getListView();
		// Enable filtering when the user types in the virtual keyboard
		lv.setTextFilterEnabled(true);
		enableAlarm();
		// Set an setOnItemClickListener on the ListView
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Create an Intent to start the ImageViewActivity
				Intent intent = new Intent(SelfieViewListActivity.this,
						ImageViewActivity.class);
				// Add the location of the thumbnail to display as an Intent
				// Extra
				intent.putExtra(EXTRA_RES_ID, ((SelfieRecord) mAdapter
						.getItem(position)).getPhotoUri());
				// Start the ImageViewActivity
				startActivity(intent);
			}
		});
		mAdapter = new SelfieViewAdapter( this, new DatabaseOpenHelper(this));
		setListAdapter(mAdapter);
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.camera:
			takeSelfie();
			return true;
		case R.id.delete_all:
			mAdapter.removeAll();
			return true;
		case R.id.delete_selected:
			mAdapter.removeSelected();
			return true;
		case R.id.cancel_alarm:
			cancelAlarm();
		    Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
			return true;			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void cancelAlarm() {
		AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				this, 0, new Intent(this, AlarmNotificationReceiver.class), 0);				
		manager.cancel(pendingIntent);
		ComponentName receiver = new ComponentName(this, AlarmBootReceiver.class);
		PackageManager pm = getPackageManager();
		pm.setComponentEnabledSetting(receiver,
		        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
		        PackageManager.DONT_KILL_APP);
	}

	private void takeSelfie() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File outputMedia = ImageUtil.getOutputMediaFile();
		mCurrentImagePath =  outputMedia.getAbsolutePath();
		Uri fileUri = Uri.fromFile(outputMedia); // create a file to save the image
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
		Log.i(this.getClass().getName(),"Output image file name:"+fileUri.getPath());
		if (intent.resolveActivity(getPackageManager()) != null) {
			// start the image capture Intent
			startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
		}
	}

	private void enableAlarm() {
		AlarmBootReceiver.setupAlarm(this);
		ComponentName receiver = new ComponentName(this, AlarmBootReceiver.class);
		PackageManager pm = getPackageManager();
		pm.setComponentEnabledSetting(receiver,
		        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
		        PackageManager.DONT_KILL_APP);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				mAdapter.add(mCurrentImagePath);
				// Image captured and saved to fileUri specified in the Intent
				Toast.makeText(
						this,"Image saved to:\n"+ mCurrentImagePath,Toast.LENGTH_LONG).show();
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
		}
	}

	/**
	 * @see Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAdapter.onDestroy();
	}
}