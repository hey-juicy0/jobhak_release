package univcapstone.employmentsite.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import univcapstone.employmentsite.domain.Post;
import univcapstone.employmentsite.domain.Reply;
import univcapstone.employmentsite.domain.User;
import univcapstone.employmentsite.dto.ReplyDto;
import univcapstone.employmentsite.repository.PostRepository;
import univcapstone.employmentsite.repository.ReplyRepository;

import java.util.List;

@Slf4j
@Transactional
@Service
public class ReplyService {

    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;

    @Autowired
    public ReplyService(PostRepository postRepository, ReplyRepository replyRepository) {
        this.postRepository = postRepository;
        this.replyRepository = replyRepository;
    }

    public Reply saveReply(Long postId, User user, ReplyDto replyDto) {

        Post post = postRepository.getReferenceById(postId);

        Reply reply = Reply.builder()
                .post(post)
                .user(user)
                .parentReplyId(replyDto.getParentReplyId())
                .replyContent(replyDto.getReplyContent())
                .build();

        replyRepository.save(reply);
        return reply;
    }

    public void deleteReply(Long replyId, User loginUser) {
        Reply findReply = replyRepository.findByReplyId(replyId);
        if(findReply==null){
            throw new IllegalStateException("이미 삭제된 댓글이거나 찾을 수 없는 댓글 입니다.");
        }
        User replyWriter = findReply.getUser();

        if (!replyWriter.getLoginId().equals(loginUser.getLoginId())) {
            throw new IllegalStateException("자신이 작성한 댓글만 삭제할 수 있습니다.");
        }

        replyRepository.delete(replyId);
    }
    public List<Reply> findReplyByLoginId(String loginId){
        return replyRepository.findByLoginId(loginId);
    }

    public Reply updateReply(Long replyId,String replyContent){
        return replyRepository.update(replyId,replyContent);

    }
}
