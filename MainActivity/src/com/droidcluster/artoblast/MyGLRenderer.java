package com.droidcluster.artoblast;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class must
 * override the OpenGL ES drawing lifecycle methods:
 * <ul>
 * <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 * <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 * <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

	private int height;
	private int width;
	private Box box;
	private long startTime = System.currentTimeMillis();
	private Float touchedX = null;
	private Float touchedY = null;
	private Context context;

	public MyGLRenderer(int height, int width, Context context) {
		super();
		this.height = height;
		this.width = width;
		this.context = context;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set the background frame color
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		box = new Box(height, width, context);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		long endTime = System.currentTimeMillis();
		long dt = endTime - startTime;
		if (dt < 16) {
			try {
				Thread.sleep(16 - dt);
			} catch (InterruptedException e) {
			}
		}
		startTime = System.currentTimeMillis();

		// Draw background color
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// Set GL_MODELVIEW transformation mode
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity(); // reset the matrix to its default state

		// When using GL_MODELVIEW, you must set the view point
		GLU.gluLookAt(gl, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

		// TODO

		if (!box.isExploding() && touchedX != null && touchedY != null) {
			box.explode(touchedX, touchedY);
			touchedX = null;
			touchedY = null;
		}
		
	//	loadGLTextureFromResource(id, context, true, gl);
		box.draw(gl);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO tuk da napravq taka, 4e ni6to da ne se promenq
		// Adjust the viewport based on geometry changes
		// such as screen rotations
		gl.glViewport(0, 0, width, height);

		// make adjustments for screen ratio
		float ratio = (float) width / height;
		gl.glMatrixMode(GL10.GL_PROJECTION); // set matrix to projection mode
		gl.glLoadIdentity(); // reset the matrix to its default state
		gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7); // apply the projection
													// matrix
		box.setHeight(height);
	}

	public void setTouchedX(float touchedX) {
		this.touchedX = touchedX;
	}

	public void setTouchedY(float touchedY) {
		this.touchedY = touchedY;
	}
	
	public void newGame() {
		
		box = new Box(height, width, context);
	}
}