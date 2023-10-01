package dev.harishjoshi.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc
public class PostControllerTest {

    private static final String API_BASE_URL = "/api/v1/posts";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostRepository postRepository;

    private List<Post> posts = new ArrayList<>();

    @BeforeEach
    void setup() {
        posts = List.of(
                new Post(1, 1, "First Post", "This is my first post.", null),
                new Post(2, 1, "Second Post", "This is my second post.", null)
        );
    }

    @Test
    void shouldFindAllPosts() throws Exception {
        var jsonResponse = """
                [
                    {
                        "id": 1,
                        "userId": 1,
                        "title": "First Post",
                        "body": "This is my first post.",
                        "version": null
                    },
                    {
                        "id": 2,
                        "userId": 1,
                        "title": "Second Post",
                        "body": "This is my second post.",
                        "version": null
                    }
                ]
                """;

        when(postRepository.findAll())
                .thenReturn(posts);

        mockMvc.perform(get(API_BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void shouldFindPostWhenGivenValidId() throws Exception {
        when(postRepository.findById(1)).thenReturn(Optional.of(posts.get(0)));

        var post = posts.get(0);
        var postJson = STR. """
                    {
                        "id":\{ post.id() },
                        "userId":\{ post.userId() },
                        "title": "\{ post.title() }",
                        "body": "\{ post.body() }",
                        "version": null
                    }
                """ ;

        mockMvc.perform(get(API_BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(postJson));
    }

    @Test
    void shouldNotFindPostWhenGivenInvalidId() throws Exception {
        when(postRepository.findById(0)).thenThrow(PostNotFoundException.class);

        mockMvc.perform(get(API_BASE_URL + "0"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewPostWhenIsValid() throws Exception {
        var post = new Post(3, 1, "New Post", "This is new post.", null);
        when(postRepository.save(post)).thenReturn(post);

        var requestBody = STR. """
                    {
                        "id":\{ post.id() },
                        "userId":\{ post.userId() },
                        "title": "\{ post.title() }",
                        "body": "\{ post.body() }",
                        "version": null
                    }
                """ ;

        mockMvc.perform(post(API_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotCreateNewPostWhenInValid() throws Exception {
        var post = new Post(3, 1, "New Post", "This is my new post.", null);
        when(postRepository.save(post)).thenReturn(post);

        var requestBody = STR. """
                    {
                        "id":\{ post.id() },
                        "userId":\{ post.userId() },
                        "title": "",
                        "body": "",
                        "version": null
                    }
                """ ;

        mockMvc.perform(post(API_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdatePostWhenValidPost() throws Exception {
        var updatedPost = new Post(1, 1, "Updated Post", "This is my updated post.", 1);
        when(postRepository.findById(1)).thenReturn(Optional.of(updatedPost));

        var requestBody = STR. """
                    {
                        "id":\{ updatedPost.id() },
                        "userId":\{ updatedPost.userId() },
                        "title": "\{ updatedPost.title() }",
                        "body": "\{ updatedPost.body() }",
                        "version":\{ updatedPost.version() }
                    }
                """ ;

        mockMvc.perform(put(API_BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestBody)
                )
                .andExpect(status().isOk());
    }

    @Test
    void ShouldDeletePostWhenGivenValidId() throws Exception {
        doNothing().when(postRepository).deleteById(1);

        mockMvc.perform(delete(API_BASE_URL + "/1"))
                .andExpect(status().isNoContent());

        verify(postRepository, times(1)).deleteById(1);
    }
}
