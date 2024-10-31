package com.lakroft.audiostorage.controller;

import com.lakroft.audiostorage.entity.Phrase;
import com.lakroft.audiostorage.repository.PhraseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/phrases")
public class PhraseController {

	private final PhraseRepository phraseRepository;

	@PostMapping
	public ResponseEntity<Phrase> createPhrase(@RequestBody Phrase phrase) {
		Phrase savedPhrase = phraseRepository.save(phrase);
		return ResponseEntity.ok(savedPhrase);
	}
}