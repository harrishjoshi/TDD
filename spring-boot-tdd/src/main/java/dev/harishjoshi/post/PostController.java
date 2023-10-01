package dev.harishjoshi.post;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/posts")
class PostController {

    private final PostRepository postRepository;

    PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping
    List<Post> findAll() {
        return postRepository.findAll();
    }

    @GetMapping("{id}")
    Optional<Post> findById(@PathVariable Integer id) {
        return Optional.ofNullable(postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Post create(@RequestBody @Valid Post post) {
        return postRepository.save(post);
    }

    @PutMapping("{id}")
    Post update(@PathVariable Integer id, @RequestBody @Valid Post post) {
        var existingPostOpt = postRepository.findById(id);
        if (existingPostOpt.isEmpty()) {
            throw new PostNotFoundException();
        }

        var updatedPost = new Post(
                existingPostOpt.get().id(),
                existingPostOpt.get().userId(),
                post.title(),
                post.body(),
                post.version()
        );

        return postRepository.save(updatedPost);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Integer id) {
        postRepository.deleteById(id);
    }
}
