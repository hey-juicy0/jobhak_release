package univcapstone.employmentsite.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import univcapstone.employmentsite.domain.ExpNCareer;
import univcapstone.employmentsite.domain.User;
import univcapstone.employmentsite.repository.ExpNCareerRepository;

import java.util.List;

@Slf4j
@Transactional
@Service
public class ExpNCareerService {
    private final ExpNCareerRepository expNCareerRepository;

    @Autowired
    public ExpNCareerService(ExpNCareerRepository expNCareerRepository) {
        this.expNCareerRepository = expNCareerRepository;
    }

    public ExpNCareer findTextByUserId(Long id) {
        return expNCareerRepository.findTextByUserId(id);
    }

    public void saveText(User user, String content) {
        if (expNCareerRepository.findTextByUserId(user.getId())== null){
            ExpNCareer expNCareer = ExpNCareer.builder()
                    .user(user)
                    .content(content)
                    .build();
            expNCareerRepository.save(expNCareer);
        }else{
            expNCareerRepository.updateText(user.getId(),content);
        }
    }
}
