package course.lab.dailyselfie;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

/**
 * Image tool to load the image or create a thumbnail for the image
 *  
 * @author rchan
 *
 */
public class ImageUtil {
	/** Create a File for saving an image or video */
	public static File getOutputMediaFile() {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir = new File(
		   Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(ImageUtil.class.getName(), "failed to create directory");
				return null;
			}
		}
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		return new File(mediaStorageDir.getPath() + File.separator + "IMG_"
				+ timeStamp + ".jpg");
	}
	/**
	 * get bipmap from the image file
	 * @param path
	 * @param resources
	 * @return
	 */
	public static Bitmap getBitmap(String path, Resources resources) {
		Bitmap imgBitmap = null;
		FileInputStream fis = null;
		if (path != null) {
			try {
				fis = new FileInputStream(path);
				imgBitmap = BitmapFactory.decodeStream(fis);
			} catch (Exception ex) {
				
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						// do nothing
					}
				}
			}
		}
		if (imgBitmap == null) {
			imgBitmap = BitmapFactory
					.decodeResource(resources, R.drawable.stub);
		}
		return imgBitmap;
	}
	/**
	 * get bitmap from file and re-size the bitmap to thumbnail size
	 * @param path
	 * @param resources
	 * @return
	 */
	public static Bitmap getBitmapThumbnail(String path, Resources resources) {
		Bitmap imgthumbBitmap = null;
		FileInputStream fis = null;
		try {
			final int THUMBNAIL_SIZE = 64;
			fis = new FileInputStream(path);
			imgthumbBitmap = BitmapFactory.decodeStream(fis);
			imgthumbBitmap = Bitmap.createScaledBitmap(imgthumbBitmap,
					THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

			ByteArrayOutputStream bytearroutstream = new ByteArrayOutputStream();
			imgthumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
					bytearroutstream);

		} catch (Exception ex) {
			
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}

		if (imgthumbBitmap == null) {
			imgthumbBitmap = BitmapFactory.decodeResource(resources,
					R.drawable.stub);
		}
		return imgthumbBitmap;
	}

}
