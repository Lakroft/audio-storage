package com.lakroft.audiostorage.repository;
import com.lakroft.audiostorage.entity.AudioFile;
import com.lakroft.audiostorage.entity.Phrase;
import com.lakroft.audiostorage.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AudioFileRepository extends JpaRepository<AudioFile, Long> {
	Optional<AudioFile> findByUserAndPhrase(User user, Phrase phrase);
}
