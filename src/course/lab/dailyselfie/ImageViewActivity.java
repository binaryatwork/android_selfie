package course.lab.dailyselfie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Activity that displays the selected selfie image
 * @author rchan
 *
 */
public class ImageViewActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		// Get the Intent used to start this Activity
		Intent intent = getIntent();
		
		// Make a new ImageView
		ImageView imageView = new ImageView(getApplicationContext());
		
		String path = intent.getStringExtra(SelfieViewListActivity.EXTRA_RES_ID);
		// Get the image to display and set it as the image for this ImageView
		imageView.setImageBitmap(ImageUtil.getBitmap(path,this.getResources()));					
		
		setContentView(imageView);
	}
	

}