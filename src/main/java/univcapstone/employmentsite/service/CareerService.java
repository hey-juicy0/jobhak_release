package univcapstone.employmentsite.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import univcapstone.employmentsite.domain.Career;
import univcapstone.employmentsite.domain.Post;
import univcapstone.employmentsite.domain.User;
import univcapstone.employmentsite.dto.CareerSaveDto;
import univcapstone.employmentsite.repository.CareerRepository;
import univcapstone.employmentsite.repository.ExpNCareerRepository;

import java.util.List;

@Slf4j
@Transactional
@Service
public class CareerService {
    private final CareerRepository careerRepository;

    @Autowired
    public CareerService(CareerRepository careerRepository) {
        this.careerRepository = careerRepository;
    }

    public List<Career> findCareerListByUserId(Long id) {
        List<Career> careers = careerRepository.findCareerListByUserId(id);
        if (careers == null) {
            return null;
        }
        return careers;
    }

    public void saveCareer(User user, List<CareerSaveDto> careerSaveDto) {
        for(CareerSaveDto careerData : careerSaveDto){
            Career career = careerData.toEntity(user,careerData);
            careerRepository.save(career);
        }
    }
}
