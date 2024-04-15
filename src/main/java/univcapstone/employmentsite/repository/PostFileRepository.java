package univcapstone.employmentsite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import univcapstone.employmentsite.domain.Picture;
import univcapstone.employmentsite.domain.PostFile;

import java.util.List;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {
    @Query("SELECT p FROM PostFile p WHERE p.post.postId=:postId")
    List<PostFile> findAllByPostId(Long postId);
}
