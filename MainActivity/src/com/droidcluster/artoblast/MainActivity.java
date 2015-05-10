package com.droidcluster.artoblast;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.droidcluster.artoblast.R;
import com.droidcluster.artoblast.MySeekBar.OnSeekBarChangeListener;
import com.droidcluster.artoblast.RangeSeekBar.OnRangeSeekBarChangeListener;

public class MainActivity extends Activity {

	private MyGLSurfaceView mGLView;
	private boolean settingsOpened = false;
	private ImageView eight;
	private MediaPlayer mediaPlayer;
	private ViewDrawer drawer;

	public static final boolean DEFAULT_8 = false;
	public static final String PREF_8 = "pref_eight";
	public static final float DEFAULT_VOLUME = 5.0f;
	public static final String PREF_VOLUME = "pref_volume";

	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();
		setContentView(R.layout.activity_main);
		toggleHideyBar();
		prefs = PreferenceManager
				.getDefaultSharedPreferences(MainActivity.this);

		mGLView = (MyGLSurfaceView) findViewById(R.id.gl_view);
		setupAnimations();
		MySoundPlayer.initSounds(getApplicationContext(),
				prefs.getBoolean(PREF_8, DEFAULT_8));
		if (!MySoundPlayer.eightBit) {
			mediaPlayer = MediaPlayer.create(this, R.raw.ambient);
		} else {
			mediaPlayer = MediaPlayer.create(this, R.raw.soundtrackeight);
		}
		mediaPlayer.start();
		mediaPlayer.setLooping(true);
		drawer = new ViewDrawer(this);

		float volume = prefs.getFloat(PREF_VOLUME, DEFAULT_VOLUME);
		mediaPlayer.setVolume(volume, volume);
	}

	public void toggleHideyBar() {
		int uiOptions = this.getWindow().getDecorView().getSystemUiVisibility();
		int newUiOptions = uiOptions;

		// Navigation bar hiding: Backwards compatible to ICS.
		if (Build.VERSION.SDK_INT >= 14) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		}
		// Status bar hiding: Backwards compatible to Jellybean
		if (Build.VERSION.SDK_INT >= 16) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
		}

		if (Build.VERSION.SDK_INT >= 18) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		}
		this.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
		mediaPlayer.pause();
		MySoundPlayer.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
		mediaPlayer.start();
		MySoundPlayer.resume();
	}

	private void setupAnimations() {
		ImageView replay = (ImageView) findViewById(R.id.replay);
		ImageView settings = (ImageView) findViewById(R.id.settings);

		replay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mGLView.newGame();
			}
		});

		settings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!settingsOpened) {
					mGLView.pause();
					openSettingsView();
				} else {
					LinearLayout layout = (LinearLayout) findViewById(R.id.settings_menu);
					layout.setVisibility(View.GONE);
					settingsOpened = false;
					mGLView.resume();
				}
			}
		});
		final ObjectAnimator anim1 = ObjectAnimator.ofFloat(replay,
				"rotationY", 0, 360);
		anim1.setDuration(1000);
		anim1.setStartDelay(10000);
		final ObjectAnimator anim2 = ObjectAnimator.ofFloat(settings,
				"rotation", 0, 360);
		anim2.setDuration(1000);
		anim2.setStartDelay(1000);
		anim2.start();
		anim1.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationCancel(Animator arg0) {
			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				anim1.setStartDelay(3000);
				anim1.start();
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
			}

			@Override
			public void onAnimationStart(Animator arg0) {
			}

		});
		anim1.start();
		anim2.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationCancel(Animator arg0) {
			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				anim1.setStartDelay(5000);
				anim2.start();
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
			}

			@Override
			public void onAnimationStart(Animator arg0) {
			}

		});
	}

	private void openSettingsView() {
		settingsOpened = true;
		RangeSeekBar<Integer> tones = (RangeSeekBar<Integer>) findViewById(R.id.tones);
		tones.setImportantValues(1, 10);
		// TODO nicely done with prefs
		tones.setSelectedMaxValue(8);
		tones.setSelectedMinValue(3);
		MySeekBar<Integer> music = (MySeekBar<Integer>) findViewById(R.id.music);
		music.setImportantValues(1, 10);
		int volume = (int) (10 * prefs.getFloat(PREF_VOLUME, DEFAULT_VOLUME));
		music.setSelectedMaxValue(volume);
		music.setOnRangeSeekBarChangeListener(new OnSeekBarChangeListener<Integer>() {

			@Override
			public void onRangeSeekBarValuesChanged(MySeekBar<?> bar,
					Integer maxValue) {
				mediaPlayer.setVolume(maxValue / 10f, maxValue / 10f);
				Editor edit = prefs.edit();
				edit.putFloat(PREF_VOLUME, (float) maxValue / 10f);
				edit.commit();
			}

		});
		tones.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
					Integer minValue, Integer maxValue) {
				// TODO handle changed range values
			}
		});
		LinearLayout layout = (LinearLayout) findViewById(R.id.settings_menu);
		LayoutParams params = layout.getLayoutParams();
		int width, height;
		WindowManager wm = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= 13) {
			Point size = new Point();
			display.getSize(size);
			width = size.x;
			height = size.y;
		} else {
			width = display.getWidth();
			height = display.getHeight();
		}
		params.width = (int) (width * 0.7);
		params.height = (int) (height * 0.5);
		layout.setLayoutParams(params);
		layout.setVisibility(View.VISIBLE);

		eight = (ImageView) findViewById(R.id.eight);
		if (prefs.getBoolean(PREF_8, DEFAULT_8)) {
			eight.setImageResource(R.drawable.eightclicked);
		}
		eight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor edit = prefs.edit();
				if (prefs.getBoolean(PREF_8, DEFAULT_8)) {
					edit.putBoolean(PREF_8, false);
					eight.setImageResource(R.drawable.eight);
					MySoundPlayer.eightBit = false;
					mediaPlayer.pause();
					mediaPlayer = MediaPlayer.create(getApplicationContext(),
							R.raw.ambient);
					mediaPlayer.start();
					mediaPlayer.setLooping(true);
					mGLView.newGame();
				} else {
					edit.putBoolean(PREF_8, true);
					eight.setImageResource(R.drawable.eightclicked);
					MySoundPlayer.eightBit = true;
					mGLView.newGame();
					mediaPlayer.pause();
					mediaPlayer = MediaPlayer.create(getApplicationContext(),
							R.raw.soundtrackeight);
					mediaPlayer.start();
					mediaPlayer.setLooping(true);
				}
				edit.commit();
			}
		});

		ImageView back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				LinearLayout layout = (LinearLayout) findViewById(R.id.settings_menu);
				layout.setVisibility(View.GONE);
				settingsOpened = false;
				mGLView.resume();
			}
		});

		ImageView rate = (ImageView) findViewById(R.id.rate);
		rate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final String appPackageName = getPackageName();
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("market://details?id=" + appPackageName)));
				} catch (android.content.ActivityNotFoundException anfe) {
					startActivity(new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("https://play.google.com/store/apps/details?id="
									+ appPackageName)));
				}
			}
		});
	}
}
