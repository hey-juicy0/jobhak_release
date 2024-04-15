package univcapstone.employmentsite.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import univcapstone.employmentsite.domain.Career;
import univcapstone.employmentsite.dto.CareerSaveDto;

import java.util.List;

@Repository
public interface CareerRepository extends JpaRepository<Career,Long> {

    @Query("SELECT c FROM Career c WHERE c.user.id = :id")
    List<Career> findCareerListByUserId(Long id);

}
