package com.lakroft.audiostorage.service;

import com.lakroft.audiostorage.entity.AudioFile;
import com.lakroft.audiostorage.entity.Phrase;
import com.lakroft.audiostorage.entity.User;
import com.lakroft.audiostorage.repository.AudioFileRepository;
import com.lakroft.audiostorage.repository.PhraseRepository;
import com.lakroft.audiostorage.repository.UserRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
		// Check if userId and phraseId exist
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid userId"));
		Phrase phrase = phraseRepository.findById(phraseId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid phraseId"));

		// Convert file to wav format
		File convertedFile = convertToWav(file);

		// Save file to disk
		String filePath = saveFileToDisk(convertedFile);

		// Save file information to the database
		AudioFile audioFile = new AudioFile();
		audioFile.setUser(user);
		audioFile.setPhrase(phrase);
		audioFile.setFilePath(filePath);
		audioFileRepository.save(audioFile);
	}

	public File getAudioFile(Long userId, Long phraseId, String format) throws IOException {
		// Check if userId and phraseId exist
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid userId"));
		Phrase phrase = phraseRepository.findById(phraseId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid phraseId"));

		AudioFile audioFile = audioFileRepository.findByUserAndPhrase(user, phrase)
			.orElseThrow(() -> new IllegalArgumentException("Audio file not found"));

		// Convert file to the requested format
		return convertToFormat(new File(audioFile.getFilePath()), format);
	}

	private File convertToWav(MultipartFile file) throws IOException {
		// Save the uploaded file to a temporary file
		Path tempFile = Files.createTempFile("upload", "." + FilenameUtils.getExtension(file.getOriginalFilename()));
		file.transferTo(tempFile.toFile());

		// Determine the path for the converted file
		Path convertedFilePath = Files.createTempFile("converted", ".wav");

		// Convert the file to wav format using ffmpeg
		FFmpegBuilder builder = new FFmpegBuilder()
			.setInput(tempFile.toString())
			.overrideOutputFiles(true)
			.addOutput(convertedFilePath.toString())
			.setFormat("wav")
			.done();

		FFmpegExecutor executor = new FFmpegExecutor(ffmpegService.getFfmpeg(), ffmpegService.getFfprobe());
		executor.createJob(builder).run();

		return convertedFilePath.toFile();
	}

	private String saveFileToDisk(File file) throws IOException {
		// Determine the path for saving the file
		Path storagePath = Paths.get(audioStoragePath);
		if (!Files.exists(storagePath)) {
			Files.createDirectories(storagePath);
		}

		Path destinationPath = storagePath.resolve(file.getName());
		Files.move(file.toPath(), destinationPath);

		return destinationPath.toString();
	}

	private File convertToFormat(File file, String format) throws IOException {
		// Determine the path for the converted file
		Path convertedFilePath = Files.createTempFile("converted", "." + format);

		// Convert the file to the requested format using ffmpeg
		FFmpegBuilder builder = new FFmpegBuilder()
			.setInput(file.getAbsolutePath())
			.overrideOutputFiles(true)
			.addOutput(convertedFilePath.toString())
			.setFormat(format)
			.done();

		FFmpegExecutor executor = new FFmpegExecutor(ffmpegService.getFfmpeg(), ffmpegService.getFfprobe());
		executor.createJob(builder).run();

		return convertedFilePath.toFile();
	}
}
