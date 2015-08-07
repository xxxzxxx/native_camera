package com.universal.robot.core.helper;

import java.math.BigInteger;
import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import android.hardware.Camera;
import android.os.Debug;
import android.util.Log;

import com.primitive.natives.camera.BuildConfig;

/**
 * Logger
 */
public class Logger {
	public enum Comparison {
		Greater, Less, EqualGreater, EqualLess, Equal,
	};

	public static class Level {
		public static final Comparison ComparisonMode = Comparison.Less;
		public static final Level Nothing = new Level(0);
		public static final Level Performance = new Level(10);
		public static final Level Error = new Level(20);
		public static final Level Warm = new Level(30);
		public static final Level Info = new Level(40);
		public static final Level Trace = new Level(50);
		public static final Level Debug = new Level(90);
		public static final Level All = new Level(100);
		private int value;

		private Level(final int value) {
			this.value = value;
		}

		public static int comparison(Level compare, Level base) {
			int result = 0;
			if (ComparisonMode == Comparison.Greater) {
				result = (compare.value < base.value) ? 0 : -1;
			} else if (ComparisonMode == Comparison.Less) {
				result = (compare.value > base.value) ? 0 : -1;
			} else if (ComparisonMode == Comparison.Equal) {
				result = (compare.value == base.value) ? 0 : -1;
			}
			return result;
		}

		public int comparison(Level base) {
			int result = 0;
			if (ComparisonMode == Comparison.Greater) {
				result = (this.value < base.value) ? 0 : -1;
			} else if (ComparisonMode == Comparison.Less) {
				result = (this.value > base.value) ? 0 : -1;
			} else if (ComparisonMode == Comparison.Equal) {
				result = (this.value == base.value) ? 0 : -1;
			}
			return result;
		}
	}

	private static Level LogLevel =
			BuildConfig.DEBUG ? Level.All : Level.Error
	;

	public static long start() {
		long started = System.currentTimeMillis();
		if (LogLevel.comparison(Logger.Level.Trace) >= 0) {
			StackTraceElement currentTack = Thread.currentThread()
					.getStackTrace()[3];
			if (currentTack != null)
				Log.i(currentTack.getClassName(), currentTack.getMethodName()
						+ " start");
		}
		return started;
	}

	public static long end() {
		long end = System.currentTimeMillis();
		if (LogLevel.comparison(Logger.Level.Trace) >= 0) {
			StackTraceElement currentTack = Thread.currentThread()
					.getStackTrace()[3];
			if (currentTack != null)
				Log.i(currentTack.getClassName(), currentTack.getMethodName()
						+ " end");
		}
		return end;
	}

	public static long end(final long started) {
		long end = System.currentTimeMillis();
		if (LogLevel.comparison(Logger.Level.Trace) >= 0) {
			StackTraceElement currentTack = Thread.currentThread()
					.getStackTrace()[3];
			if (currentTack != null)
				Log.i(currentTack.getClassName(), currentTack.getMethodName()
						+ " end[" + (end - started) + "ms]");
		}
		return end;
	}

	public static void info(Object obj) {
		if (LogLevel.comparison(Logger.Level.Info) >= 0) {
			StackTraceElement currentTack = Thread.currentThread()
					.getStackTrace()[3];
			if (currentTack != null)
				Log.i(currentTack.getClassName(), obj != null ? obj.toString()
						: "obj is null");
		}
	}

	public static void info(String format, Object... args) {
		if (LogLevel.comparison(Logger.Level.Info) >= 0) {
			StackTraceElement currentTack = Thread.currentThread()
					.getStackTrace()[3];
			if (currentTack != null) {
				if (args != null && args.length > 0) {
					Log.i(currentTack.getClassName(),
							String.format(format, args));
				} else {
					Log.i(currentTack.getClassName(), format);
				}
			}
		}
	}

	public static void err(Throwable ex) {
		if (LogLevel.comparison(Logger.Level.Error) >= 0) {
			StackTraceElement currentTack = Thread.currentThread()
					.getStackTrace()[3];
			if (currentTack != null) {
				String message = ex.getMessage();
				output(Level.Error, currentTack, message);
			}
		}
	}

	public static void warm(String format, Object... args) {
		if (LogLevel.comparison(Logger.Level.Warm) >= 0) {
			StackTraceElement currentTack = Thread.currentThread()
					.getStackTrace()[3];
			if (currentTack != null) {
				if (args != null && args.length > 0) {
					Log.w(currentTack.getClassName(),
							String.format(format, args));
				} else {
					Log.w(currentTack.getClassName(), format);
				}
			}
		}
	}

	public static void warm(Throwable ex) {
		if (LogLevel.comparison(Logger.Level.Warm) >= 0) {
			StackTraceElement currentTack = Thread.currentThread()
					.getStackTrace()[3];
			if (currentTack != null)
				Log.w(currentTack.getClassName(), ex.getStackTrace().toString());
		}
	}

	public static void debug(String format, Object... args) {
		if (LogLevel.comparison(Logger.Level.Debug) >= 0) {
			StackTraceElement currentTack = Thread.currentThread()
					.getStackTrace()[3];
			if (currentTack != null) {
				if (args != null && args.length > 0) {
					Log.d(currentTack.getClassName(),
							String.format(format, args));
				} else {
					Log.d(currentTack.getClassName(), format);
				}
			}
		}
	}

	public static void times(final long started) {
		final long endl = System.currentTimeMillis();
		if (LogLevel.comparison(Logger.Level.Performance) >= 0) {
			StackTraceElement currentTack = Thread.currentThread()
					.getStackTrace()[3];
			if (currentTack != null) {
				String msg = "Process Time:" + (endl - started);
				Log.i(currentTack.getClassName(), msg);
			}
		}
	}

	public static void times(final long started, final long endl) {
		if (LogLevel.comparison(Logger.Level.Performance) >= 0) {
			StackTraceElement currentTack = Thread.currentThread()
					.getStackTrace()[3];
			if (currentTack != null) {
				String msg = "Process Time:" + (endl - started);
				Log.i(currentTack.getClassName(), msg);
			}
		}
	}

	public static long heap() {
		final long free = Debug.getNativeHeapFreeSize();
		final long allocated = Debug.getNativeHeapAllocatedSize();
		final long heap = Debug.getNativeHeapSize();
		StackTraceElement currentTack = Thread.currentThread().getStackTrace()[3];
		String msg = String.format(
				"heap : Free=%d kb\n Allocated=%d kb\n Size=%d", free,
				allocated, heap);
		if (currentTack != null)
			Log.v(currentTack.getClassName(), msg);
		return heap;
	}

	public static boolean isNull(final String tag, final Object arg) {
		if (LogLevel.comparison(Logger.Level.Debug) >= 0) {
			StackTraceElement currentTack = Thread.currentThread()
					.getStackTrace()[3];
			if (currentTack != null) {
				String message = (arg != null) ? String.format(
						"[%s] is not null:[%s]", tag, arg.getClass()
								.getSimpleName()) : String.format(
						"[%s] is null", tag);
				output(Level.Debug, currentTack, message);
			}
		}
		return (arg == null);
	}

	private static void output(final Logger.Level level,
			final StackTraceElement currentTack, final String message) {
		final String className = currentTack.getClassName();
		// final int lineNumber = currentTack.getLineNumber();
		// final String fileName = currentTack.getFileName();
		// final String methodName = currentTack.getMethodName();
		final String msg = message != null ? message : "null";// String.format("%s.%s(%s:%d)\n%s",
																// className,
																// methodName,
																// fileName,
																// lineNumber,
																// message);
		if (level == Level.Trace)
			Log.i(className, msg);
		if (level == Level.Info)
			Log.i(className, msg);
		if (level == Level.Error)
			Log.e(className, msg);
		if (level == Level.Debug)
			Log.d(className, msg);
		if (level == Level.Warm)
			Log.w(className, msg);
		if (level == Level.Performance)
			Log.i(className, msg);
	}

	public static void debug(final String tag, final Certificate[] certificates) {
		for (final Certificate cert : certificates) {
			debug(tag, cert);
		}
	}

	public static void debug(final String tag, final Certificate certificate) {
		Logger.debug("-------------- Certificate -------------");
		try {
			final X509Certificate x509 = (certificate instanceof X509Certificate) ? ((X509Certificate) certificate)
					: null;

			Logger.debug("%s Type:[%s]", tag, certificate.getType());
			Logger.debug(tag, certificate.getPublicKey());
			if (x509 != null) {
				Logger.debug(x509.getIssuerDN());
				Logger.debug("CriticalExtensionOID:[%s]",
						x509.getCriticalExtensionOIDs());
				try {
					Collection<String> usage = x509.getExtendedKeyUsage();
					Logger.debug("ExtendedKeyUsage:[%s]", usage);
				} catch (final Throwable ex) {
					Logger.err(ex);
				}
				Logger.debug("NotAfter", x509.getNotAfter());
				Logger.debug("NotBefore", x509.getNotBefore());
				Logger.debug("SerialNumber", x509.getSerialNumber());
			}
		} finally {
			Logger.debug("-------------- Certificate -------------");
		}
	}

	public static void debug(final String tag, final BigInteger integer) {
		if (integer != null) {
			Logger.debug("%s bit count: [%d]", tag, integer.bitCount());
			Logger.debug("%s bit length: [%d]", tag, integer.bitLength());
			Logger.debug("%s byte value: [%s]", tag, integer.byteValue());
			Logger.debug("%s doubleValue: [%l]", tag, integer.doubleValue());
		}
	}

	public static void debug(final String tag, final Date date) {
		if (date != null) {
			final SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");
			Logger.debug("%s: [%s]", tag, dateFormat.format(date));
		} else {
			Logger.debug("%s: [is null]", tag);
		}
	}

	public static void debug(final String tag, final PublicKey publicKey) {
		final String type = "Public Key";
		if (publicKey != null) {
			Logger.debug("%s %s Algorithm: [%s]", tag, type,
					publicKey.getAlgorithm());
			Logger.debug("%s %s Format: [%s]", tag, type, publicKey.getFormat());
			Logger.debug("%s %s Encode: [%s]", tag, type,
					publicKey.getEncoded());
		} else {
			Logger.debug("%s %s: [is null]", tag, type);
		}
	}

	public static void debug(final String format, final Collection<String> sets) {
		for (final String value : sets) {
			Logger.debug(format, value);
		}
	}

	public static void debug(final byte[] bytes)
	{
		StringBuffer buffer = new StringBuffer(256);
		int count = 0;
		int rotation = 0;
		for (final byte value : bytes) {
			buffer.append(String.format("%2x ",value));
			if (count >= 256)
			{
				Logger.debug("%8d:%s", rotation,buffer.toString());
				buffer.setLength(0);
				count = 0;
				rotation ++;
			}
			else
			{
				count++;
			}
		}
		if (buffer.length() > 0)
		{
			Logger.debug("%8d:%s", buffer.toString());
		}
	}

	public static void debug(final Principal principal) {
		final String tag = "Principal";
		if (principal != null) {
			Logger.debug("%s Algorithm: [%s]", tag, principal.getName());
		} else {
			Logger.debug("%s: [is null]", tag);
		}
	}

	@SuppressWarnings("deprecation")
	public static void camerainfo() {
		Camera c = null;
		try {
			c = Camera.open();
			Logger.debug("", c.getParameters().flatten());
		} catch (Throwable ex) {
			Logger.err(ex);
		} finally {
			if (c != null)
				c.release();
		}
	}
}