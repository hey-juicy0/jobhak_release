package univcapstone.employmentsite.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import univcapstone.employmentsite.domain.Resume;
import univcapstone.employmentsite.domain.User;
import univcapstone.employmentsite.repository.ResumeRepository;

import java.util.List;

@Slf4j
@Transactional
@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;

    @Autowired
    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public List<Resume> getMyResume(Long userId){
        return resumeRepository.getResumeListByUserId(userId);
    }

    public Resume saveResume(User user, String content) {
        Resume resume=new Resume(user.getId(),content);
        resumeRepository.save(resume);
        return resume;
    }

    public void reviseResume(Long resumeId, String content) {
        Resume resume=resumeRepository.getResumeByResumeId(resumeId);
        resumeRepository.updateResume(resume.getResumeId(),content);
    }
}
