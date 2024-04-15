package univcapstone.employmentsite.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import univcapstone.employmentsite.dto.BookmarkDeleteDto;
import univcapstone.employmentsite.service.BookmarkService;
import univcapstone.employmentsite.util.response.BasicResponse;
import univcapstone.employmentsite.util.response.DefaultResponse;
import univcapstone.employmentsite.util.response.ErrorResponse;

@Slf4j
@RestController
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Autowired
    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    /**
     * 북마크 삭제
     *
     * @param bookmarkData
     * @return
     */
    @DeleteMapping(value = "/user/bookmark/delete")
    public ResponseEntity<? extends BasicResponse> editUser(
            HttpServletRequest request,
            @RequestBody @Validated BookmarkDeleteDto bookmarkData
    ) {
        try {
            bookmarkService.deleteBookmark(bookmarkData.getBookmarkId());

            DefaultResponse<String> defaultResponse = DefaultResponse.<String>builder()
                    .code(HttpStatus.OK.value())
                    .httpStatus(HttpStatus.OK)
                    .message("북마크 삭제완료.")
                    .result("")
                    .build();

            return ResponseEntity.ok().body(defaultResponse);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(request.getServletPath(),
                            HttpStatus.BAD_REQUEST.value(),
                            "북마크 삭제 실패"));
        }

    }

}
