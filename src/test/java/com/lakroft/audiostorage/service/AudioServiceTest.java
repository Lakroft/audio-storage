package com.lakroft.audiostorage.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.Optional;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
	"audio.storage.path=${java.io.tmpdir}/audio_storage_test"
})
public class AudioServiceTest {

	@MockBean
	private AudioFileRepository audioFileRepository;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private PhraseRepository phraseRepository;

	@MockBean
	private FFmpegService ffmpegService;

	@Autowired
	private AudioService audioService;

	@Mock
	private MultipartFile multipartFile;

	@Mock
	private FFmpeg ffmpeg;

	@Mock
	private FFprobe ffprobe;

	@BeforeEach
	public void setUp() throws IOException {
		when(ffmpegService.getFfmpeg()).thenReturn(ffmpeg);
		when(ffmpegService.getFfprobe()).thenReturn(ffprobe);
	}

	@Test
	public void testSaveAudioFile() throws IOException {
		User user = new User();
		user.setId(1L);
		Phrase phrase = new Phrase();
		phrase.setId(1L);

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
		when(phraseRepository.findById(anyLong())).thenReturn(Optional.of(phrase));
		when(multipartFile.getOriginalFilename()).thenReturn("test.m4a");
		doAnswer(invocation -> {
			File file = invocation.getArgument(0);
			if (!file.exists()) {
				Files.createFile(file.toPath());
			}
			return null;
		}).when(multipartFile).transferTo(any(File.class));

		audioService.saveAudioFile(1L, 1L, multipartFile);

		verify(audioFileRepository, times(1)).save(any(AudioFile.class));

		// Clean up temporary directory
		Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "audio_storage_test");
		Files.walk(tempDir)
			.map(Path::toFile)
			.forEach(File::delete);
	}

	@Test
	public void testConvertToFormat() throws IOException {
		File inputFile = Files.createTempFile("input", ".m4a").toFile();
		String format = "wav";

		File result = audioService.convertToFormat(inputFile, format);

		assertNotNull(result);
		assertTrue(result.getName().endsWith(".wav"));
	}
}
