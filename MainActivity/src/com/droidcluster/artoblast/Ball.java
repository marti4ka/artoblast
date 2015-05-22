package com.droidcluster.artoblast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Ball {
	private boolean exploding = false;
	private boolean exploded = false;
	// normalized translation vector
	private float translationX;
	private float translationY;

	// circle variables
	private int circlePoints = 35;
	private float radius = 0.02f;
	private float explosionRadius = 0.2f;
	private float diff;

	// outer vertices of the circle i.e. excluding the center_x,center_y
	int circumferencePoints = circlePoints - 1;
	// circle vertices and buffer variables
	float circleVertices[] = new float[circlePoints * 2];
	private FloatBuffer ballBuff; // 4bytes per float

	// color values
	float color[] = new float[4];
	private float maxH;
	private float maxW;

	public Ball(float centerX, float centerY) {
		// the initial buffer values
		circleVertices[0] = centerX;
		circleVertices[1] = centerY;

		computeVertices();

		addVerticesToBuffer();
	}

	private void computeVertices() {
		for (int i = 0, vertices = 2; i < circumferencePoints; i++) {
			// set circle vertices values
			float percent = (i / (float) (circumferencePoints - 1));
			float radians = (float) (percent * 2 * Math.PI);

			// vertex position
			float outer_x = (float) (circleVertices[0] + radius
					* Math.cos(radians));
			float outer_y = (float) (circleVertices[1] + radius
					* Math.sin(radians));

			circleVertices[vertices++] = outer_x;
			circleVertices[vertices++] = outer_y;
		}
	}

	// draw methods
	public void draw(GL10 gl) {
		// get the front face
		gl.glFrontFace(GL10.GL_CW); // front facing is clockwise
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		// pointer to the buffer
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, ballBuff);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4f(color[0], color[1], color[2], color[3]);

		// draw circle as filled shape
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, circlePoints);

		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}

	public void translate() {
		for (int i = 0; i < circleVertices.length;) {
			circleVertices[i++] += translationX;
			circleVertices[i++] += translationY;
		}
		if (Math.abs(circleVertices[0]) + radius > maxW) {
			translationX *= (-1);
			// TODO MySoundPlayer.playSound(MySoundPlayer.bass);
		}
		if (Math.abs(circleVertices[1]) + radius > maxH) {
			translationY *= (-1);
			// TODO MySoundPlayer.playSound(MySoundPlayer.bass);
		}

		addVerticesToBuffer();
	}

	private void addVerticesToBuffer() {
		ByteBuffer ballByteBuff = ByteBuffer
				.allocateDirect(circleVertices.length * 4);
		// garbage collector wont throw this away
		ballByteBuff.order(ByteOrder.nativeOrder());
		ballBuff = ballByteBuff.asFloatBuffer();
		ballBuff.put(circleVertices);
		ballBuff.position(0);
	}

	public void explode() {
		if (radius > explosionRadius) {
			exploding = false;
			exploded = true;
		}
		radius += diff / 5;
		computeVertices();
		addVerticesToBuffer();
		color[3] -= diff;
	}

	public void setTranslation(float transX, float transY) {
		this.translationX = transX * 0.008f;
		this.translationY = transY * 0.008f;
		// deal with too slow balls
		if (Math.abs(translationX) + Math.abs(translationY) < 0.003) {
			this.translationX *= 1.5;
			this.translationY *= 1.5;
		}
		diff = (float) Math.sqrt(translationX * translationX + translationY
				* translationY);
	}

	public void setColor(float r, float g, float b) {
		color[0] = r;
		color[1] = g;
		color[2] = b;
		color[3] = 1.0f;
	}

	public void setMaxH(float maxH) {
		this.maxH = maxH;
	}

	public void setMaxW(float maxW) {
		this.maxW = maxW;
	}

	public boolean isExploding() {
		return exploding;
	}

	public void setExploding(boolean exploding) {
		this.exploding = exploding;
	}

	public boolean isExploded() {
		return exploded;
	}

	public void setExploded(boolean exploded) {
		this.exploded = exploded;
	}

	public float distanceTo(float x, float y) {
		return (float) Math.sqrt(Math.pow(circleVertices[0] - x, 2)
				+ Math.pow(circleVertices[1] - y, 2));
	}

	public float getCenterX() {
		return circleVertices[0];
	}

	public float getCenterY() {
		return circleVertices[1];
	}

	public float getRadius() {
		return radius;
	}

	public int getExplodingTime() {
		// V = diff/5 S = 0.2 - 0.02
		return (int) ((explosionRadius - radius) / (diff / 5)) + 5;
	}
}