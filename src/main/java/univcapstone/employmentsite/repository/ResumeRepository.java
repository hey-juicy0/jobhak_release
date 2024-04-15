package univcapstone.employmentsite.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import univcapstone.employmentsite.domain.Resume;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ResumeRepository {
    private final EntityManager em;

    public void save(Resume resume) {
        em.persist(resume);
    }

    public List<Resume> getResumeListByUserId(Long userId) {
        List<Resume> resumes = em.createQuery("select r from Resume r where r.userId=:userId",Resume.class)
                .setParameter("userId",userId)
                .getResultList();

        return resumes;
    }

    public Resume getResumeByResumeId(Long resumeId) {
        Resume resume = em.createQuery("select r from Resume r where r.resumeId=:resumeId",Resume.class)
                .setParameter("resumeId",resumeId)
                .getSingleResult();

        return resume;
    }

    public void updateResume(Long resumeId,String content) {
        Resume resume = em.find(Resume.class, resumeId);
        resume.setResumeContent(content);
    }
}

