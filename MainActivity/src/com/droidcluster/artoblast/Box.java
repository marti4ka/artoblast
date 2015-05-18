package com.droidcluster.artoblast;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Box {
	private Ball[] balls;
	private int height;
	private int width;
	private boolean exploding = false;
	private int totalCount;
	private Handler uiHandler;
	private MainActivity context;
	private int t;
	private int exploded;

	public Box(int h, int w, Context context) {
		height = h;
		width = w;
		int max = (h * w) / (30 * (h + w));
		int count = new Random().nextInt(max);
		totalCount = count + 35;
		balls = new Ball[totalCount];
		generateBalls();
		MySoundPlayer.i = 0;
		// TODO
		MySoundPlayer.tones = MySoundPlayer.r.nextInt(3);
		uiHandler = new Handler(context.getMainLooper());
		this.context = (MainActivity) context;
		exploded = 0;
		// TODO hide wellDone and tryAgain

		hideTextView();
	}

	private void hideTextView() {
		uiHandler.post(new Runnable() {
			@Override
			public void run() {
				LinearLayout finalView = (LinearLayout) context
						.findViewById(R.id.score_layout);
				finalView.setVisibility(View.GONE);
			}
		});

	}

	private void generateBalls() {
		for (int i = 0; i < balls.length; i++) {
			Random r = new Random();
			// Ball b = new Ball(r.nextFloat() * 2 - 1, r.nextFloat() * 2 *
			// width
			// / height - width / height);
			Ball b = new Ball(0, 0);
			b.setMaxH(1);
			// TODO maxW??
			b.setMaxW(width / (float) (height + 55));
			// b.setCenter(r.nextFloat() * 2, r.nextFloat() * 2 * width /
			// height);
			float transX, transY;
			if (r.nextInt(2) % 2 == 0) {
				transX = r.nextFloat();
			} else {
				transX = (-1) * r.nextFloat();
			}
			if (r.nextInt(2) % 2 == 0) {
				transY = r.nextFloat();
			} else {
				transY = (-1) * r.nextFloat();
			}
			b.setTranslation(transX, transY);
			if (!MySoundPlayer.eightBit) {
				b.setColor(r.nextFloat(), r.nextFloat(), r.nextFloat());
			} else {
				switch (r.nextInt(3)) {
				case 0:
					b.setColor(1.0f, 0.0f, 0.0f);
					break;
				case 1:
					b.setColor(0.0f, 1.0f, 0.0f);
					break;
				case 2:
					b.setColor(0.0f, 0.0f, 1.0f);
					break;
				case 3:
					b.setColor(1.0f, 1.0f, 1.0f);
					break;
				default:
					break;
				}

			}
			balls[i] = b;
		}

	}

	public void draw(GL10 gl) {
		if (exploding) {
			if (--t == 0) {
				displayFinalView();
			}
			detectColisions();
		}
		for (Ball b : balls) {
			if (!b.isExploding() && !b.isExploded()) {
				b.translate();
				b.draw(gl);
			}
			if (b.isExploding() && !b.isExploded()) {
				b.explode();
				b.draw(gl);
			}
		}
	}

	private void displayFinalView() {
		uiHandler.post(new Runnable() {
			@Override
			public void run() {
				final ImageView replay = (ImageView) context
						.findViewById(R.id.replay);
				replay.setVisibility(View.GONE);
				final ImageView settings = (ImageView) context
						.findViewById(R.id.settings);
				settings.setVisibility(View.GONE);

				LinearLayout finalView = (LinearLayout) context
						.findViewById(R.id.score_layout);
				TextView score = (TextView) context
						.findViewById(R.id.final_view_count);
				score.setText((exploded) + "/" + (totalCount + exploded - 1)
						+ " blasted");

				ImageView bigreplay = (ImageView) context
						.findViewById(R.id.bigreplay);
				bigreplay.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						settings.setVisibility(View.VISIBLE);
						replay.setVisibility(View.VISIBLE);
						context.mGLView.newGame();
					}
				});

				finalView.setVisibility(View.VISIBLE);
			}
		});
	}

	private void detectColisions() {
		for (int i = 0; i < balls.length; i++) {
			if (balls[i].isExploding()) {
				for (int j = 0; j < balls.length; j++) {
					if (!balls[j].isExploded()
							&& !balls[j].isExploding()
							&& balls[i].distanceTo(balls[j].getCenterX(),
									balls[j].getCenterY()) <= balls[i]
									.getRadius() + balls[j].getRadius()) {
						balls[j].setExploding(true);
						MySoundPlayer.playSound();
						int time = balls[j].getExplodingTime();
						if (t < time) {
							t = time;
						}
						balls[j].explode();
						exploded++;
						if (--totalCount == 0) {
							displayFinalView();
						}
					}
				}

			}
		}
	}

	public void explode(float x, float y) {
		// transform coordinates
		y = 1 - 2 * (y / height);
		x = (-1) * (((2 * x) - width) / height);

		// checkNeigborhood
		for (Ball b : balls) {
			if (b.distanceTo(x, y) < 0.08f) {
				b.setExploding(true);
				b.explode();
				t = b.getExplodingTime();
				exploding = true;
				exploded++;
				return;
			}
		}
	}

	public boolean isExploding() {
		return exploding;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
