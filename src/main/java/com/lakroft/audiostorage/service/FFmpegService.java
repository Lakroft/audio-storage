package com.lakroft.audiostorage.service;

import java.io.IOException;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FFmpegService {

	private final FFmpeg ffmpeg;
	private final FFprobe ffprobe;

	public FFmpegService(@Value("${ffmpeg.path:/usr/bin/ffmpeg}") String ffmpegPath,
		@Value("${ffprobe.path:/usr/bin/ffprobe}") String ffprobePath) {
		try {
			this.ffmpeg = new FFmpeg(ffmpegPath);
			this.ffprobe = new FFprobe(ffprobePath);
		} catch (IOException e) {
			throw new RuntimeException("Failed to initialize FFmpeg or FFprobe", e);
		}
	}

	public FFmpeg getFfmpeg() {
		return ffmpeg;
	}

	public FFprobe getFfprobe() {
		return ffprobe;
	}
}
