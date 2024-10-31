package com.lakroft.audiostorage.controller;

import com.lakroft.audiostorage.service.AudioService;
import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/audio")
public class AudioController {

	private final AudioService audioService;

	@PostMapping("/user/{userId}/phrase/{phraseId}")
	public ResponseEntity<Void> uploadAudio(@PathVariable Long userId, @PathVariable Long phraseId,
		@RequestParam("audio_file") MultipartFile file) {
		try {
			audioService.saveAudioFile(userId, phraseId, file);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			log.error("Audio upload exception:", e);
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/user/{userId}/phrase/{phraseId}/{audioFormat}")
	public ResponseEntity<Resource> downloadAudio(@PathVariable Long userId, @PathVariable Long phraseId,
		@PathVariable String audioFormat) {
		try {
			File file = audioService.getAudioFile(userId, phraseId, audioFormat);
			Resource resource = new FileSystemResource(file);
			return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType("audio/" + audioFormat))
				.body(resource);
		} catch (Exception e) {
			log.error("Audio download exception:", e);
			return ResponseEntity.notFound().build();
		}
	}
}
