package com.droidcluster.artoblast;

import java.util.Timer;
import java.util.TimerTask;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.droidcluster.artoblast.MySeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity {

	protected MyGLSurfaceView mGLView;
	private boolean settingsOpened = false;
	private ImageView eight;
	private MediaPlayer mediaPlayer;

	public static final boolean DEFAULT_8 = false;
	public static final String PREF_8 = "pref_eight";
	public static final float DEFAULT_VOLUME = 5.0f;
	public static final String PREF_VOLUME = "pref_volume";

	private SharedPreferences prefs;
	private boolean paused;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();
		setContentView(R.layout.activity_main);
        setFontface();
		toggleHideyBar();
		prefs = PreferenceManager
				.getDefaultSharedPreferences(MainActivity.this);

		mGLView = (MyGLSurfaceView) findViewById(R.id.gl_view);
		setupAnimations();
		
        ViewTreeObserver viewTreeObserver = mGLView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                	mGLView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    new AsyncTask() {
						@Override
						protected Object doInBackground(Object... params) {
							MySoundPlayer.initSounds(getApplicationContext(),
									prefs.getBoolean(PREF_8, DEFAULT_8));
							if (!MySoundPlayer.eightBit) {
								mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.ambient);
							} else {
								mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.soundtrackeight);
							}
							return null;
						}
						
						protected void onPostExecute(Object result) {
							mediaPlayer.setVolume(0, 0);
							mediaPlayer.start();
							mediaPlayer.setLooping(true);
							final float volume = prefs.getFloat(PREF_VOLUME, DEFAULT_VOLUME);
							
							if(paused) {
								mediaPlayer.pause();
								mediaPlayer.setVolume(volume, volume);
							} else {
								int fadeSteps = 10;
								final Timer timer = new Timer(true);
								TimerTask timerTask = new TimerTask() {
									float vol = 0;
									@Override
									public void run() {
										vol += 0.005;
										mediaPlayer.setVolume(vol, vol);
										
										if (vol >= volume) {
											timer.cancel();
											timer.purge();
										}
									}
								};
								timer.schedule(timerTask, 50, 50);
							}
							
						};
                    	
                    }.execute();
                }
            });
        }
	}

	private void setFontface() {
		Typeface tf = Typeface.createFromAsset(getAssets(), "2-d638a.ttf");
        TextView finalView = (TextView) findViewById(R.id.final_view);
        finalView.setTypeface(tf);
        TextView finalViewCount = (TextView) findViewById(R.id.final_view_count);
        finalViewCount.setTypeface(tf);
        TextView appTitle = (TextView) findViewById(R.id.app_title);
        appTitle.setTypeface(tf);
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
		paused = true;
		mGLView.onPause();
		if (mediaPlayer != null) {
			mediaPlayer.pause();
			MySoundPlayer.pause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		paused = false;
		mGLView.onResume();
		if (mediaPlayer != null) {
			mediaPlayer.start();
			MySoundPlayer.resume();
		}
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
				LinearLayout score = (LinearLayout) findViewById(R.id.score_layout);
				if (score.getVisibility() != View.GONE) {
					return;
				}
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
		final ObjectAnimator replayAnim = ObjectAnimator.ofFloat(replay,
				"rotation", 0, 360);
		replayAnim.setDuration(900);
		replayAnim.setStartDelay(10000);
		final ObjectAnimator settingsAnim = ObjectAnimator.ofFloat(settings,
				"rotation", 0, 360);
		settingsAnim.setDuration(900);
		settingsAnim.setStartDelay(5000);
		settingsAnim.start();
		replayAnim.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationCancel(Animator arg0) {
			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				replayAnim.setStartDelay(7000);
				replayAnim.start();
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
			}

			@Override
			public void onAnimationStart(Animator arg0) {
			}

		});
		replayAnim.start();
		settingsAnim.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationCancel(Animator arg0) {
			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				settingsAnim.setStartDelay(5000);
				settingsAnim.start();
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
		
		// TODO nicely done with prefs
//		RangeSeekBar<Integer> tones = (RangeSeekBar<Integer>) findViewById(R.id.tones);
//		tones.setImportantValues(1, 10);
//		tones.setSelectedMaxValue(8);
//		tones.setSelectedMinValue(3);
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
//		tones.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
		// @Override
		// public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
		// Integer minValue, Integer maxValue) {
		// // TODO handle changed range values
		// }
		//	});
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
