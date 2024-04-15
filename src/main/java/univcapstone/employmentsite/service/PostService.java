package univcapstone.employmentsite.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import univcapstone.employmentsite.domain.Post;
import univcapstone.employmentsite.domain.User;
import univcapstone.employmentsite.dto.PostDto;
import univcapstone.employmentsite.repository.BookmarkRepository;
import univcapstone.employmentsite.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Transactional
@Service
public class PostService {

    private final PostRepository postRepository;
    private final BookmarkRepository bookmarkRepository;

    @Autowired
    public PostService(PostRepository postRepository, BookmarkRepository bookmarkRepository) {
        this.postRepository = postRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    public Post findPostById(long postId) {
        return postRepository.findByPostId(postId);
    }

    public List<Post> showAllPost(Pageable pageable) {
        List<Post> post = new ArrayList<>();
        Page<Post> pagePost = postRepository.findAllByOrderByDateDesc(pageable);

        if (pagePost != null && pagePost.hasContent()) {
            post = pagePost.getContent();
        }
        return post;
    }

    public Post showPost(Long postId) {
        Post post = postRepository.findByPostId(postId);
        if (post == null) {
            throw new IllegalStateException("해당하는 게시글을 찾을 수 없습니다.");
        }
        return post;
    }

    public void deletePost(Long postId) {
        Post post = postRepository.findByPostId(postId);
        if (post == null) {
            throw new IllegalStateException("해당하는 게시글을 찾을 수 없습니다.");
        }
        postRepository.delete(post);
    }

    public Post uploadPost(User user, PostDto postData) {
        Post post = postData.toEntity(user, postData);
        postRepository.save(post);
        return post;
    }

    public void updatePost(Long postId, PostDto postDto) {
        Post findPost = postRepository.findByPostId(postId);
        findPost.setTitle(postDto.getTitle());
        findPost.setContent(postDto.getContent());
        postRepository.save(findPost);
    }

    public List<Post> searchByTitle(String boardTitle) {
        List<Post> postResult = postRepository.findByTitle(boardTitle);
        return postResult;
    }

    public List<Post> showResumePostOrderByDate(Pageable pageable) {
        List<Post> post = new ArrayList<>();
        Page<Post> pagePost = postRepository.findResumeByOrderByDateDesc(pageable);

        if (pagePost != null && pagePost.hasContent()) {
            post = pagePost.getContent();
        }
        return post;
    }

    public List<Post> showInterviewPostOrderByDate(Pageable pageable) {
        List<Post> post = new ArrayList<>();
        Page<Post> pagePost = postRepository.findInterviewByOrderByDateDesc(pageable);

        if (pagePost != null && pagePost.hasContent()) {
            post = pagePost.getContent();
        }
        return post;
    }

    public List<Post> showSharePostOrderByDate(Pageable pageable) {
        List<Post> post = new ArrayList<>();
        Page<Post> pagePost = postRepository.findShareByOrderByDateDesc(pageable);

        if (pagePost != null && pagePost.hasContent()) {
            post = pagePost.getContent();
        }
        return post;
    }

    public List<Post> showAllPostByPopluar() {
        List<Post> post = bookmarkRepository.getAllPostByPopular();
        return post;
    }

    public List<Post> showResumePostOrderByPopular(Pageable pageable) {
        List<Post> post = bookmarkRepository.getResumePostOrderByPopular();
        return post;
    }

    public List<Post> showInterviewPostOrderByPopular(Pageable pageable) {
        List<Post> post = bookmarkRepository.getInterviewPostOrderByPopular();
        return post;
    }

    public List<Post> showSharePostOrderByPopular(Pageable pageable) {
        List<Post> post = bookmarkRepository.getSharePostOrderByPopular();
        return post;
    }

    public List<Post> showMyPost(String loginId) {
        List<Post> post = postRepository.findMyPost(loginId);
        return post;
    }
}