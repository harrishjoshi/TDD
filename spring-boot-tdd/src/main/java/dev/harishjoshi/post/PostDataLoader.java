package dev.harishjoshi.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.asm.TypeReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PostDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PostDataLoader.class);
    private final ObjectMapper objectMapper;
    private final PostRepository postRepository;

    public PostDataLoader(ObjectMapper objectMapper, PostRepository postRepository) {
        this.objectMapper = objectMapper;
        this.postRepository = postRepository;
    }

    @Override
    public void run(String... args) {
        if (postRepository.count() == 0) {
            var postsJson = "/data/posts.json";
            log.info("Loading posts into the database from JSON: {}", postsJson);
            try (var inputStream = TypeReference.class.getResourceAsStream(postsJson)) {
                var response = objectMapper.readValue(inputStream, Posts.class);
                postRepository.saveAll(response.posts());
            } catch (IOException e) {
                throw new RuntimeException("Failed to read JSON data", e);
            }
        }
    }
}
