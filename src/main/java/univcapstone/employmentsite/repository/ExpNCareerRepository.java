package univcapstone.employmentsite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import univcapstone.employmentsite.domain.ExpNCareer;

import java.util.List;

@Repository
public interface ExpNCareerRepository extends JpaRepository<ExpNCareer,Long> {

    @Query("SELECT e FROM ExpNCareer e WHERE e.user.id = :id")
    ExpNCareer findTextByUserId(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE ExpNCareer e SET e.content = :content WHERE e.user.id = :id")
    void updateText(Long id,String content);
}
