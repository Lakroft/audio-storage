package com.lakroft.audiostorage.util;

import com.lakroft.audiostorage.constant.AudioFormat;
import lombok.experimental.UtilityClass;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;

@UtilityClass
public class AudioFormatUtil {
	public static FFmpegOutputBuilder configureCodec(FFmpegOutputBuilder builder, String format) {
		AudioFormat audioFormat = AudioFormat.fromString(format);
		builder.setAudioCodec(audioFormat.getCodec());
		if (audioFormat.getBitRate() > 0) {
			builder.setAudioBitRate(audioFormat.getBitRate());
		}
		return builder;
	}
}
