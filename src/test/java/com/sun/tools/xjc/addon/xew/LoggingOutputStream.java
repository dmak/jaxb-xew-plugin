package com.sun.tools.xjc.addon.xew;

import java.io.OutputStream;

import org.apache.commons.logging.Log;

/**
 * Class will redirect everything printed to this {@link OutputStream} to logger.
 * 
 * @author Dmitry Katsubo
 */
class LoggingOutputStream extends OutputStream {

	public enum LogLevel {
		TRACE, DEBUG, INFO, WARN, ERROR, FATAL
	}

	private final StringBuilder				   sb = new StringBuilder();

	private final Log						   logger;
	private final LoggingOutputStream.LogLevel logLevel;
	private final String					   messagePrefix;

	public LoggingOutputStream(Log logger, LoggingOutputStream.LogLevel logLevel) {
		this(logger, logLevel, null);
	}

	public LoggingOutputStream(Log logger, LoggingOutputStream.LogLevel logLevel, String messagePrefix) {
		this.logger = logger;
		this.logLevel = logLevel;
		this.messagePrefix = messagePrefix;
	}

	@Override
	public void write(byte[] buf, int off, int len) {
		for (int i = 0; i < len; i++) {
			write(buf[off + i]);
		}
	}

	@Override
	public void write(int b) {
		// Scan all input bytes and log a message on newline:
		switch (b) {
		case '\n':
			logMessage();

		case '\r':
			break;

		default:
			sb.append((char) b);
		}
	}

	@Override
	public void close() {
		if (sb.length() > 0) {
			logMessage();
		}
	}

	private void logMessage() {
		if (messagePrefix != null) {
			sb.insert(0, messagePrefix);
		}

		String message = sb.toString();

		switch (logLevel) {
		case TRACE:
			logger.trace(message);
			break;
		case DEBUG:
			logger.debug(message);
			break;
		case INFO:
			logger.info(message);
			break;
		case WARN:
			logger.warn(message);
			break;
		case ERROR:
			logger.error(message);
			break;
		case FATAL:
			logger.fatal(message);
			break;
		}

		sb.setLength(0);
	}
}