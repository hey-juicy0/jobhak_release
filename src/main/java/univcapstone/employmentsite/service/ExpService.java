package univcapstone.employmentsite.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import univcapstone.employmentsite.domain.Experience;
import univcapstone.employmentsite.domain.User;
import univcapstone.employmentsite.dto.ExpSaveDto;
import univcapstone.employmentsite.repository.ExpNCareerRepository;
import univcapstone.employmentsite.repository.ExpRepository;

import java.util.List;

@Slf4j
@Transactional
@Service
public class ExpService {
    private final ExpRepository expRepository;

    @Autowired
    public ExpService(ExpRepository expRepository) {
        this.expRepository = expRepository;
    }

    public List<Experience> findExpListByUserId(Long id) {
        return expRepository.findExpListByUserId(id);
    }

    public void saveExp(User user, List<ExpSaveDto> expSaveDto) {
        for(ExpSaveDto expData : expSaveDto){
            Experience exp = expData.toEntity(user,expData);
            expRepository.save(exp);
        }

    }
}
