package com.droidcluster.artoblast;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

/**
 * A view container where OpenGL ES graphics can be drawn on screen. This view
 * can also be used to capture touch events, such as a user interacting with
 * drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

	private MyGLRenderer mRenderer;

	public MyGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MyGLSurfaceView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		int width, height;
		WindowManager wm = (WindowManager) context
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

		// Set the Renderer for drawing on the GLSurfaceView
		mRenderer = new MyGLRenderer(height, width);
		setRenderer(mRenderer);

		// Render the view only when there is a change in the drawing data
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mRenderer.setTouchedX(e.getX());
			mRenderer.setTouchedY(e.getY());
		}
		return true;
	}

	public void newGame() {
		mRenderer.newGame();
		requestRender();
	}

	public void pause() {
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	public void resume() {
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}
}
