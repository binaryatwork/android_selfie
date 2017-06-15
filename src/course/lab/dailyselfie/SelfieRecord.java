package course.lab.dailyselfie;

import java.util.Calendar;

import android.graphics.Bitmap;

/**
 * selfie record information container
 * @author rchan
 *
 */
public class SelfieRecord {
	private String mPhotoUri;
	private Calendar mTimeStamp;
	private Bitmap mBitmap;

	public SelfieRecord(Calendar calendar, String photoUri) {
		this.setPhotoUri(photoUri);
		this.setTimeStamp(calendar);		
	}

	@Override
	public String toString(){
		return "Photo file: " + getPhotoUri() + " TimeStamp: " + getTimeStamp();		
	}

	public String getPhotoUri() {
		return mPhotoUri;
	}



	public void setPhotoUri(String mPhotoUri) {
		this.mPhotoUri = mPhotoUri;
	}

	public Calendar getTimeStamp() {
		return mTimeStamp;
	}

	public void setTimeStamp(Calendar mTimeStamp) {
		this.mTimeStamp = mTimeStamp;
	}

	public Bitmap getThumbnailBitmap() {
		return mBitmap;
	}
	
	public void setThumbnailBitmap(Bitmap mBitmap) {
		this.mBitmap = mBitmap;
	}
}
