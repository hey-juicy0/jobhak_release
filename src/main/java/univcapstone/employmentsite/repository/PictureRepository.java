package univcapstone.employmentsite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import univcapstone.employmentsite.domain.Picture;
import univcapstone.employmentsite.domain.User;

import java.util.List;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> {

    @Query("SELECT p FROM Picture p WHERE p.user.id=:userId")
    List<Picture> findAllByUserId(Long userId);

    @Query("SELECT p FROM Picture p WHERE p.user.id=:userId and p.isProfile=true")
    Picture findAllByProfile(Long userId);

    @Query("SELECT p FROM Picture p WHERE p.user.id=:userId and p.isProfile=true")
    List<Picture> findAllByProfiles(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Picture p SET p.isProfile = false WHERE p.user.id = :userId")
    void setProfileFalse(Long userId);

    @Modifying
    @Query("DELETE FROM Picture p WHERE p.isProfile=true AND p.user.id = :userId")
    void deleteFormerProfile(Long userId);
}
