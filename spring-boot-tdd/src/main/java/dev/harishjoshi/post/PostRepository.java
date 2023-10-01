package dev.harishjoshi.post;

import org.springframework.data.repository.ListCrudRepository;

public interface PostRepository extends ListCrudRepository<Post, Integer> {
}
