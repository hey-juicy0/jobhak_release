package univcapstone.employmentsite.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import univcapstone.employmentsite.domain.Reply;
import univcapstone.employmentsite.domain.User;
import univcapstone.employmentsite.dto.ReplyDto;
import univcapstone.employmentsite.dto.ReplyUpdateDto;
import univcapstone.employmentsite.service.ReplyService;
import univcapstone.employmentsite.token.CustomUserDetails;
import univcapstone.employmentsite.util.response.BasicResponse;
import univcapstone.employmentsite.util.response.DefaultResponse;

@Slf4j
@RestController
public class ReplyController {
    private final ReplyService replyService;

    @Autowired
    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }

    /**
     * 댓글 달기
     *
     * @param postId
     * @param customUserDetails
     * @param replyDto
     * @return
     */
    @PostMapping("/boardlist/detail/{postId}/reply")
    public ResponseEntity<? extends BasicResponse> reply(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody @Validated ReplyDto replyDto
    ) {

        User user = customUserDetails.getUser();

        replyService.saveReply(postId, user, replyDto);
        log.info("댓글을 작성한 포스트 id = {}, 댓글을 작성한 유저 = {}, 작성한 댓글 정보 = {}", postId, user.getLoginId(), replyDto);

        DefaultResponse<ReplyDto> defaultResponse = DefaultResponse.<ReplyDto>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("댓글 작성")
                .result(replyDto)
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }

    /**
     * 댓글 삭제
     * 현재는 대댓글의 부모 댓글이 삭제되어도 대댓글은 삭제되지 않는다.
     * (게시글이 삭제되면 해당 게시글에 작성된 댓글들은 모두 삭제됨)
     *
     * @param postId
     * @param replyId
     * @return
     */
    @DeleteMapping("/boardlist/{postId}/reply/delete/{replyId}")
    public ResponseEntity<? extends BasicResponse> replyDelete(
            @PathVariable Long postId,
            @PathVariable Long replyId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        User user = customUserDetails.getUser();

        try {
            replyService.deleteReply(replyId, user);
        } catch (IllegalStateException e) {
            log.info("댓글 작성자와 삭제하려는 이가 일치하지 않거나 이미 삭제된 댓글입니다.");

            DefaultResponse<String> defaultResponse = DefaultResponse.<String>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("잘못된 삭제요청")
                    .result("")
                    .build();

            return ResponseEntity.ok().body(defaultResponse);
            /*
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
                    
             */
        }
        log.info("지우려는 댓글의 게시글 postId={}, 지우려는 댓글의 replyId={}", postId, replyId);

        // 응답 객체 생성이 중복된다.
        // refactor idea
        // 1. service 단의 기능이 성공하면 boolean 값을 넘어오게 하고
        // 해당 boolean 값과 (응답하려는) 클래스를 넘겨서 응답 객체 생성하게 끔?
        // (성공 시 DefaultResponse, 실패 시 ErrorResponse)
        DefaultResponse<Long> defaultResponse = DefaultResponse.<Long>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("댓글 삭제 성공")
                .result(replyId)
                .build();

        return ResponseEntity.ok().body(defaultResponse);
    }

    @PatchMapping("/boardlist/detail/{postId}/reply")
    public ResponseEntity<? extends BasicResponse> replyUpdate(
            @PathVariable Long postId,
            @RequestBody @Validated ReplyUpdateDto replyUpdateDto
    ) {
        Reply reply=replyService.updateReply(replyUpdateDto.getReplyId(),replyUpdateDto.getReplyContent());
        log.info("수정한 댓글ID,댓글내용 = {} {}",reply.getReplyId(),reply.getReplyContent());
        DefaultResponse<String> defaultResponse = DefaultResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("댓글 수정 성공")
                .result("")
                .build();

        return ResponseEntity.ok().body(defaultResponse);
    }
}
