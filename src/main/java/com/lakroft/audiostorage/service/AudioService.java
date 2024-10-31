package com.lakroft.audiostorage.service;

import com.lakroft.audiostorage.util.AudioFormatUtil;
import com.lakroft.audiostorage.entity.AudioFile;
import com.lakroft.audiostorage.entity.Phrase;
import com.lakroft.audiostorage.entity.User;
import com.lakroft.audiostorage.repository.AudioFileRepository;
import com.lakroft.audiostorage.repository.PhraseRepository;
import com.lakroft.audiostorage.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class AudioService {

	private final AudioFileRepository audioFileRepository;
	private final UserRepository userRepository;
	private final PhraseRepository phraseRepository;
	private final FFmpegService ffmpegService;

	@Value("${audio.storage.path}")
	private String audioStoragePath;

	public void saveAudioFile(Long userId, Long phraseId, MultipartFile file) throws IOException {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid userId: " + userId));
		Phrase phrase = phraseRepository.findById(phraseId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid phraseId: " + phraseId));

		File convertedFile = convertToFormat(file, "wav");
		String filePath = saveFileToDisk(convertedFile);

		AudioFile audioFile = new AudioFile();
		audioFile.setUser(user);
		audioFile.setPhrase(phrase);
		audioFile.setFilePath(filePath);
		audioFileRepository.save(audioFile);
	}

	public File getAudioFile(Long userId, Long phraseId, String format) throws IOException {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid userId: " + userId));
		Phrase phrase = phraseRepository.findById(phraseId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid phraseId: " + phraseId));

		AudioFile audioFile = audioFileRepository.findByUserAndPhrase(user, phrase)
			.orElseThrow(() -> new IllegalArgumentException("Audio file not found for userId: " + userId + " and phraseId: " + phraseId));

		return convertToFormat(new File(audioFile.getFilePath()), format);
	}

	private File convertToFormat(MultipartFile file, String format) throws IOException {
		Path tempFile = Files.createTempFile("upload_", "." + FilenameUtils.getExtension(file.getOriginalFilename()));
		file.transferTo(tempFile.toFile());

		try {
			return convertToFormat(tempFile.toFile(), format);
		} finally {
			Files.deleteIfExists(tempFile);
		}
	}

	File convertToFormat(File file, String format) throws IOException {
		String currentFormat = FilenameUtils.getExtension(file.getName());
		if (currentFormat.equalsIgnoreCase(format)) {
			return file;
		}

		Path convertedFilePath = Files.createTempFile("converted_", "." + format);
		try {
			FFmpegOutputBuilder outputBuilder = new FFmpegBuilder()
				.setInput(file.getAbsolutePath())
				.overrideOutputFiles(true)
				.addOutput(convertedFilePath.toString());
			FFmpegBuilder builder = AudioFormatUtil.configureCodec(outputBuilder, format).done();

			FFmpegExecutor executor = new FFmpegExecutor(ffmpegService.getFfmpeg(), ffmpegService.getFfprobe());
			executor.createJob(builder).run();

			return convertedFilePath.toFile();
		} catch (Exception e) {
			Files.deleteIfExists(convertedFilePath);
			throw new IOException("Failed to convert file to format: " + format, e);
		}
	}

	private String saveFileToDisk(File file) throws IOException {
		Path storagePath = Paths.get(audioStoragePath);
		if (!Files.exists(storagePath)) {
			Files.createDirectories(storagePath);
		}

		Path destinationPath = storagePath.resolve(file.getName());
		Files.move(file.toPath(), destinationPath);

		return destinationPath.toString();
	}
}
