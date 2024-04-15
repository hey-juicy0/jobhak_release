package univcapstone.employmentsite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import univcapstone.employmentsite.domain.Experience;

import java.util.List;

@Repository
public interface ExpRepository extends JpaRepository<Experience,Long> {
    @Query("SELECT e FROM Experience e WHERE e.user.id = :id")
    List<Experience> findExpListByUserId(Long id);
}
