-- Create table for User
CREATE TABLE app_user (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          user_name VARCHAR(255)
);

-- Create table for Phrase
CREATE TABLE phrase (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        phrase_text VARCHAR(255)
);

-- Create table for AudioFile
CREATE TABLE audio_file (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            user_id BIGINT NOT NULL,
                            phrase_id BIGINT NOT NULL,
                            file_path VARCHAR(255) NOT NULL,
                            CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES app_user(id),
                            CONSTRAINT fk_phrase FOREIGN KEY (phrase_id) REFERENCES phrase(id)
);

-- Insert initial user
INSERT INTO app_user (id, user_name) VALUES (1, 'Initial User');

-- Insert initial phrase
INSERT INTO phrase (id, phrase_text) VALUES (1, 'Initial Phrase');
