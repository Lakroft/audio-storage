package com.lakroft.audiostorage.constant;

public enum AudioFormat {
	M4A("aac", 192000),
	MP3("libmp3lame", 192000),
	WAV("pcm_s16le", 0);

	private final String codec;
	private final int bitRate;

	AudioFormat(String codec, int bitRate) {
		this.codec = codec;
		this.bitRate = bitRate;
	}

	public String getCodec() {
		return codec;
	}

	public int getBitRate() {
		return bitRate;
	}

	public static AudioFormat fromString(String format) {
		for (AudioFormat audioFormat : AudioFormat.values()) {
			if (audioFormat.name().equalsIgnoreCase(format)) {
				return audioFormat;
			}
		}
		throw new IllegalArgumentException("Unsupported format: " + format);
	}
}