package com.lakroft.audiostorage.repository;

import com.lakroft.audiostorage.entity.Phrase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhraseRepository extends JpaRepository<Phrase, Long> {}

