package com.droidcluster.artoblast;

import java.util.HashMap;
import java.util.Random;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import com.droidcluster.artoblast.R;

public class MySoundPlayer {
	
	public static final int bass = R.raw.bass;

	public static final int[] eight = { R.raw.bup0, R.raw.bup1, R.raw.bup2 };
	public static final int[] piano = { R.raw.p1a, R.raw.p1b, R.raw.p1c,
			R.raw.p1d, R.raw.p1e, R.raw.p1f, R.raw.p1g, R.raw.p2a, R.raw.p2b,
			R.raw.p2c, R.raw.p2d, R.raw.p2e, R.raw.p2f, R.raw.p2g, R.raw.p3a,
			R.raw.p3b, R.raw.p3c, R.raw.p3d, R.raw.p3e, R.raw.p3f, R.raw.p3g,
			R.raw.p4a, R.raw.p4b, R.raw.p4c, R.raw.p4d, R.raw.p4e, R.raw.p4f,
			R.raw.p4g, R.raw.p5a, R.raw.p4g, R.raw.p4f, R.raw.p4e, R.raw.p4d,
			R.raw.p4c, R.raw.p4b, R.raw.p4a, R.raw.p3g, R.raw.p3f, R.raw.p3e,
			R.raw.p3d, R.raw.p3c, R.raw.p3b, R.raw.p3a, R.raw.p2g, R.raw.p2f,
			R.raw.p2e, R.raw.p2d, R.raw.p2c, R.raw.p2b, R.raw.p2a, R.raw.p1g,
			R.raw.p1f, R.raw.p1e, R.raw.p1d, R.raw.p1c, R.raw.p1b, R.raw.p1a };

	public static final int[] melody1 = { R.raw.p2e, R.raw.p2d, R.raw.p2c,
			R.raw.p2d, R.raw.p2e, R.raw.p2e, R.raw.p2e, R.raw.p2d, R.raw.p2d,
			R.raw.p2d, R.raw.p2e, R.raw.p2e, R.raw.p2e, R.raw.p2e, R.raw.p2d,
			R.raw.p2c, R.raw.p2d, R.raw.p2e, R.raw.p2e, R.raw.p2e, R.raw.p2e,
			R.raw.p2d, R.raw.p2d, R.raw.p2e, R.raw.p2d, R.raw.p2c, R.raw.p3e,
			R.raw.p3d, R.raw.p3c, R.raw.p3d, R.raw.p3e, R.raw.p3e, R.raw.p3e,
			R.raw.p3d, R.raw.p3d, R.raw.p3d, R.raw.p3e, R.raw.p3e, R.raw.p3e,
			R.raw.p3e, R.raw.p3d, R.raw.p3c, R.raw.p3d, R.raw.p3e, R.raw.p3e,
			R.raw.p3e, R.raw.p3e, R.raw.p3d, R.raw.p3d, R.raw.p3e, R.raw.p3d,
			R.raw.p3c };

	public static final int[] melody2 = { R.raw.p2g, R.raw.p2a, R.raw.p2g,
			R.raw.p2f, R.raw.p2e, R.raw.p2f, R.raw.p2g, R.raw.p2d, R.raw.p2e,
			R.raw.p2f, R.raw.p2e, R.raw.p2f, R.raw.p2g, R.raw.p2g, R.raw.p2a,
			R.raw.p2g, R.raw.p2f, R.raw.p2e, R.raw.p2f, R.raw.p2g, R.raw.p2d,
			R.raw.p2g, R.raw.p2e, R.raw.p2c };

	public static final int[] melody3 = { R.raw.p3g, R.raw.p4c, R.raw.p4c,
			R.raw.p4c, R.raw.p4d, R.raw.p4e, R.raw.p4e, R.raw.p4e, R.raw.p4d,
			R.raw.p4c, R.raw.p4d, R.raw.p4e, R.raw.p4c, R.raw.p4e, R.raw.p4e,
			R.raw.p4f, R.raw.p4g, R.raw.p4g, R.raw.p4f, R.raw.p4e, R.raw.p4f,
			R.raw.p4g, R.raw.p4e, R.raw.p4c, R.raw.p4c, R.raw.p4d, R.raw.p4e,
			R.raw.p4e, R.raw.p4d, R.raw.p4c, R.raw.p4d, R.raw.p4e, R.raw.p4c,
			R.raw.p3g, R.raw.p3g, R.raw.p4c, R.raw.p4c, R.raw.p4c, R.raw.p4d,
			R.raw.p4e, R.raw.p4e, R.raw.p4e, R.raw.p4d, R.raw.p4c, R.raw.p4d,
			R.raw.p4e, R.raw.p4c };

	public static final int[] melody4 = { R.raw.p4c, R.raw.p4c, R.raw.p4c,
			R.raw.p3g, R.raw.p3a, R.raw.p3a, R.raw.p3g, R.raw.p4e, R.raw.p4e,
			R.raw.p4d, R.raw.p4d, R.raw.p4c, R.raw.p3g, R.raw.p4c, R.raw.p4c,
			R.raw.p4c, R.raw.p3g, R.raw.p3a, R.raw.p3a, R.raw.p3g, R.raw.p4e,
			R.raw.p4e, R.raw.p4d, R.raw.p4d, R.raw.p4c };

	public static final int[] drops = { R.raw.drop0, R.raw.drop1, R.raw.drop2,
			R.raw.drop3, R.raw.drop4, R.raw.drop5, R.raw.drop6, R.raw.drop7,
			R.raw.drop8, R.raw.drop9, R.raw.drop10 };

	public static final Random r = new Random();
	public static boolean eightBit = false;
	public static int i = 0;
	public static int tones = 0;

	private static SoundPool soundPool;
	private static HashMap soundPoolMap;

	/** Populate the SoundPool */
	public static void initSounds(Context context, boolean eightBits) {
		eightBit = eightBits;
		soundPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 0);
		soundPoolMap = new HashMap(3);

		soundPoolMap.put(eight[0], soundPool.load(context, R.raw.bup0, 0));
		soundPoolMap.put(eight[1], soundPool.load(context, R.raw.bup1, 0));
		soundPoolMap.put(eight[2], soundPool.load(context, R.raw.bup2, 0));

		soundPoolMap.put(drops[0], soundPool.load(context, R.raw.drop0, 0));
		soundPoolMap.put(drops[1], soundPool.load(context, R.raw.drop1, 0));
		soundPoolMap.put(drops[2], soundPool.load(context, R.raw.drop2, 0));
		soundPoolMap.put(drops[3], soundPool.load(context, R.raw.drop3, 0));
		soundPoolMap.put(drops[4], soundPool.load(context, R.raw.drop4, 0));
		soundPoolMap.put(drops[5], soundPool.load(context, R.raw.drop5, 0));
		soundPoolMap.put(drops[6], soundPool.load(context, R.raw.drop6, 0));
		soundPoolMap.put(drops[7], soundPool.load(context, R.raw.drop7, 0));
		soundPoolMap.put(drops[8], soundPool.load(context, R.raw.drop8, 0));
		soundPoolMap.put(drops[9], soundPool.load(context, R.raw.drop9, 0));
		soundPoolMap.put(drops[10], soundPool.load(context, R.raw.drop10, 0));

		soundPoolMap.put(piano[0], soundPool.load(context, R.raw.p1a, 0));
		soundPoolMap.put(piano[1], soundPool.load(context, R.raw.p1b, 0));
		soundPoolMap.put(piano[2], soundPool.load(context, R.raw.p1c, 0));
		soundPoolMap.put(piano[3], soundPool.load(context, R.raw.p1d, 0));
		soundPoolMap.put(piano[4], soundPool.load(context, R.raw.p1e, 0));
		soundPoolMap.put(piano[5], soundPool.load(context, R.raw.p1f, 0));
		soundPoolMap.put(piano[6], soundPool.load(context, R.raw.p1g, 0));

		soundPoolMap.put(piano[7], soundPool.load(context, R.raw.p2a, 0));
		soundPoolMap.put(piano[8], soundPool.load(context, R.raw.p2b, 0));
		soundPoolMap.put(piano[9], soundPool.load(context, R.raw.p2c, 0));
		soundPoolMap.put(piano[10], soundPool.load(context, R.raw.p2d, 0));
		soundPoolMap.put(piano[11], soundPool.load(context, R.raw.p2e, 0));
		soundPoolMap.put(piano[12], soundPool.load(context, R.raw.p2f, 0));
		soundPoolMap.put(piano[13], soundPool.load(context, R.raw.p2g, 0));

		soundPoolMap.put(piano[14], soundPool.load(context, R.raw.p3a, 0));
		soundPoolMap.put(piano[15], soundPool.load(context, R.raw.p3b, 0));
		soundPoolMap.put(piano[16], soundPool.load(context, R.raw.p3c, 0));
		soundPoolMap.put(piano[17], soundPool.load(context, R.raw.p3d, 0));
		soundPoolMap.put(piano[18], soundPool.load(context, R.raw.p3e, 0));
		soundPoolMap.put(piano[19], soundPool.load(context, R.raw.p3f, 0));
		soundPoolMap.put(piano[20], soundPool.load(context, R.raw.p3g, 0));

		soundPoolMap.put(piano[21], soundPool.load(context, R.raw.p4a, 0));
		soundPoolMap.put(piano[22], soundPool.load(context, R.raw.p4b, 0));
		soundPoolMap.put(piano[23], soundPool.load(context, R.raw.p4c, 0));
		soundPoolMap.put(piano[24], soundPool.load(context, R.raw.p4d, 0));
		soundPoolMap.put(piano[25], soundPool.load(context, R.raw.p4e, 0));
		soundPoolMap.put(piano[26], soundPool.load(context, R.raw.p4f, 0));
		soundPoolMap.put(piano[27], soundPool.load(context, R.raw.p4g, 0));

		soundPoolMap.put(piano[28], soundPool.load(context, R.raw.p5a, 0));
		soundPoolMap.put(bass, soundPool.load(context, R.raw.bass, 0));
	}

	public static void playSound() {
		if (eightBit) {
			playSound(eight[r.nextInt(3)]);
		} else {
			switch (tones) {
			case 0:
				// drops
				playSound(drops[r.nextInt(11)]);
				break;
			case 1:
				// piano random
				playSound(piano[r.nextInt(piano.length/2)]);
				break;
			case 2:
				// piano cool
				playSound(piano[i % piano.length]);
				i++;
				break;
//			case 3:
//				// melodies
//				playSound(melody1[i % melody1.length]);
//				i++;
//				break;
//			case 4:
//				// melodies
//				playSound(melody2[i % melody2.length]);
//				i++;
//				break;
//			case 5:
//				// melodies
//				playSound(melody3[i % melody3.length]);
//				i++;
//				break;
//			case 6:
//				// melodies
//				playSound(melody4[i % melody4.length]);
//				i++;
//				break;

			default:
				break;
			}
		}
	}

	/** Play a given sound in the soundPool */
	public static void playSound(int soundID) {
		if (soundPool != null && soundPoolMap != null) {
			float volume = 5.0f;
			// play sound with same right and left volume, with a priority of 1,
			// zero repeats (i.e play once), and a playback rate of 1f
			soundPool.play((Integer) soundPoolMap.get(soundID), volume, volume,
					1, 0, 1f);
		}
	}

	public static void pause() {
		soundPool.autoPause();
	}

	public static void resume() {
		soundPool.autoResume();
	}
}
