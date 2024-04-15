package univcapstone.employmentsite.repository;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import univcapstone.employmentsite.domain.Bookmark;
import univcapstone.employmentsite.domain.Post;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkRepository {
    private final EntityManager em; //JPA

    public void save(Bookmark bookmark) {
        em.persist(bookmark);
    }

    public void delete(Bookmark bookmark){
        em.remove(bookmark);
    }

    public Bookmark findByBookmarkId(Long bookmarkId){
        return em.find(Bookmark.class,bookmarkId);
    }

    public List<Bookmark> getMyBookmark(Long userId){
        List<Bookmark> bookmarks=em.createQuery("select b from Bookmark b where b.user.id = :userId", Bookmark.class)
                .setParameter("userId", userId)
                .getResultList();

        return bookmarks;
    }

    public List<Post> getAllPostByPopular() {
        String sql = "SELECT p " +
                "FROM Post p " +
                "LEFT JOIN Bookmark b ON p.postId = b.post.postId " +
                "GROUP BY p.postId " +
                "ORDER BY COUNT(b.bookmarkId) DESC";

        return em.createQuery(sql, Post.class).getResultList();
    }

    public List<Post> getResumePostOrderByPopular() {
        String sql = "SELECT p " +
                "FROM Post p " +
                "LEFT JOIN Bookmark b ON p.postId = b.post.postId " +
                "WHERE p.category = 'resume' " +
                "GROUP BY p.postId " +
                "ORDER BY COUNT(b.bookmarkId) DESC";

        return em.createQuery(sql, Post.class).getResultList();
    }

    public List<Post> getInterviewPostOrderByPopular() {
        String sql = "SELECT p " +
                "FROM Post p " +
                "LEFT JOIN Bookmark b ON p.postId = b.post.postId " +
                "WHERE p.category = 'interview' " +
                "GROUP BY p.postId " +
                "ORDER BY COUNT(b.bookmarkId) DESC";

        return em.createQuery(sql, Post.class).getResultList();
    }

    public List<Post> getSharePostOrderByPopular() {
        String sql = "SELECT p " +
                "FROM Post p " +
                "LEFT JOIN Bookmark b ON p.postId = b.post.postId " +
                "WHERE p.category = 'share' " +
                "GROUP BY p.postId " +
                "ORDER BY COUNT(b.bookmarkId) DESC";

        return em.createQuery(sql, Post.class).getResultList();
    }
}


