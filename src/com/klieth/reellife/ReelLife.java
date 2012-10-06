package com.klieth.reellife;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class ReelLife extends Activity {

	Facebook facebook = new Facebook("151531084991823");
	AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);

	private SharedPreferences mPrefs;
	
	Activity c;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reel_life);

		mPrefs = getPreferences(MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);
		if (access_token != null) {
			facebook.setAccessToken(access_token);
		}
		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}
		
		c = this;

		if (!facebook.isSessionValid()) {
			facebook.authorize(this, new String[] { "user_photos",
					"photo_upload" }, new DialogListener() {
				public void onComplete(Bundle values) {
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putString("access_token", facebook.getAccessToken());
					editor.putLong("access_expires",
							facebook.getAccessExpires());
					editor.commit();
				}

				public void onFacebookError(FacebookError error) {
				}

				public void onError(DialogError e) {
				}

				public void onCancel() {
				}
			});
		}
		
		Button button = (Button) findViewById(R.id.testButton);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mAsyncRunner.request("me/albums", new FirstRequestListener());
			}
		});
		
	}
	
	public class FirstRequestListener implements RequestListener {
		private String parseId(String json) {
			String ret = null;
			
			Log.d("first return", json);
			
			try {
				JSONObject obj = new JSONObject(json);
				
				JSONArray data = obj.getJSONArray("data");
				
				for (int i = 0; i < data.length(); i++) {
					JSONObject album = data.getJSONObject(i);
					if (album.getString("name").equals("Profile Pictures")) {
						ret = album.getString("id");
						Log.d("id", ret);
						return ret;
					}
				}
				
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return ret;
		}
		
		public void onComplete(String response, Object state) {
			mAsyncRunner.request(parseId(response) + "/photos", new SecondRequestListener());
		}
		public void onIOException(IOException e, Object state) {
		}
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
		}
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
		}
		public void onFacebookError(FacebookError e, Object state) {
		}
	}
	
	public class SecondRequestListener implements RequestListener {
		public String[] parseURLs(String json) {
			String[] ret = null;
			try {
				JSONObject o = new JSONObject(json);
				JSONArray data = o.getJSONArray("data");
				
				ret = new String[data.length()];
				
				for (int i = 0; i < data.length(); i++) {
					JSONObject photo = data.getJSONObject(i);
					ret[i] = photo.getString("source");
				}
				
				Log.d("urls", Arrays.toString(ret));

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ret;
		}
		
		public void onComplete(String response, Object state) {
			String[] urls = parseURLs(response);
			final LinearLayout ll = new LinearLayout(c);
			ListView lv = new ListView(c);
			ll.addView(lv);
			Bitmap[] values = new Bitmap[urls.length];
			ImageArrayAdapter iaa = new ImageArrayAdapter(c,new String[] {""},new Bitmap[] {Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)});
			for (int i = 0; i < urls.length; i++) {
				Bitmap bm = null;
				try {
					bm = BitmapFactory.decodeStream((InputStream) new URL(urls[i]).getContent());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if (bm == null) {
					Log.d("error", "bm is null for " + urls[i]);
					continue;
				}
				
				iaa.add(bm, "test");
			}
			lv.setAdapter(iaa);
			runOnUiThread(new Runnable() {
				public void run() {
					setContentView(ll);
				}
			});
			Log.d("done!", "reached the end.");
		}
		public void onIOException(IOException e, Object state) {
		}
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
		}
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
		}
		public void onFacebookError(FacebookError e, Object state) {
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		facebook.extendAccessTokenIfNeeded(this, null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebook.authorizeCallback(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_reel_life, menu);
		return true;
	}
}
