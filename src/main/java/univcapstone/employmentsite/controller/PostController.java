package univcapstone.employmentsite.controller;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import univcapstone.employmentsite.domain.Bookmark;
import univcapstone.employmentsite.domain.Post;
import univcapstone.employmentsite.domain.Reply;
import univcapstone.employmentsite.domain.User;
import univcapstone.employmentsite.dto.PostDetailToFrontDto;
import univcapstone.employmentsite.dto.PostDto;
import univcapstone.employmentsite.dto.PostToFrontDto;
import univcapstone.employmentsite.dto.ReplyToFrontDto;
import univcapstone.employmentsite.service.*;
import univcapstone.employmentsite.token.CustomUserDetails;
import univcapstone.employmentsite.util.response.BasicResponse;
import univcapstone.employmentsite.util.response.DefaultResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class PostController {

    private final PostService postService;
    private final BookmarkService bookmarkService;
    private final ReplyService replyService;
    private final PostFileService postFileService;
    private final PictureService pictureService;
    private final UserService userService;
    private final String dirName;

    @Autowired
    public PostController(PostService postService,
                          BookmarkService bookmarkService,
                          ReplyService replyService,
                          PostFileService postFileService,
                          PictureService pictureService,
                          UserService userService,
                          @Value("${aws.s3.post.dirName}")String dirName) {
        this.postService = postService;
        this.bookmarkService = bookmarkService;
        this.replyService = replyService;
        this.postFileService = postFileService;
        this.pictureService=pictureService;
        this.userService=userService;
        this.dirName = dirName;
    }

    /**
     * 게시글 목록(/boardlist 경로는 /boardlist/0(=메인페이지) /boardlist/1 (게시글 1페이지 목록.. 10개씩?)
     *
     * @return
     */
    @GetMapping("/boardlist")
    public ResponseEntity<? extends BasicResponse> boardMain(
            @RequestParam(required = false, defaultValue = "0", value = "page") int pageNo,
            @RequestParam(required = false, defaultValue = "latest", value = "sort") String sort,
            @RequestParam(required = false, defaultValue = "all", value = "category") String category
    ) {
        log.info("page={} , category={}", pageNo, category);

        PageRequest pageRequest = PageRequest.of(pageNo, 10);

        List<Post> posts = new ArrayList<>();

        //latest: 최신순 popular : 인기순 (북마크와의 조인순)
        if (sort.equals("latest")) {
            if (category.equals("all")) {
                posts = postService.showAllPost(pageRequest);
            } else if (category.equals("resume")) {
                posts = postService.showResumePostOrderByDate(pageRequest);
            } else if (category.equals("interview")) {
                posts = postService.showInterviewPostOrderByDate(pageRequest);
            } else if (category.equals("share")) {
                posts = postService.showSharePostOrderByDate(pageRequest);
            }
        } else if (sort.equals("popular")) {
            if (category.equals("all")) {
                posts = postService.showAllPostByPopluar();
            } else if (category.equals("resume")) {
                posts = postService.showResumePostOrderByPopular(pageRequest);
            } else if (category.equals("interview")) {
                posts = postService.showInterviewPostOrderByPopular(pageRequest);
            } else if (category.equals("share")) {
                posts = postService.showSharePostOrderByPopular(pageRequest);
            }
        }

        List<PostToFrontDto> postToFront = new ArrayList<>();

        for (Post post : posts) {
            postToFront.add(Post.convertPostDTO(post));
        }

        log.info("전체 게시글 데이터 = {}", postToFront);

        DefaultResponse<List<PostToFrontDto>> defaultResponse = DefaultResponse.<List<PostToFrontDto>>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("보여줄 게시글 데이터")
                .result(postToFront)
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }

    @GetMapping("/boardlist/detail/{postId}")
    public ResponseEntity<? extends BasicResponse> board(
            @PathVariable(name = "postId") Long postId) {
        // 게시글을 찾고 게시글 작성자의 정보를 찾음
        Post post = postService.showPost(postId);
        Map<String,String> postImageURL=postFileService.findFileByPostId(postId);
        Long writerId=post.getUser().getId();
        User writer=userService.findUserById(writerId);
        String writerProfile=pictureService.getProfileImage(writer);
        Map<String,String> writerProfileName=pictureService.getProfileImageName(writer);
        log.info("클릭한 게시물 정보:{} , 게시글의 이미지 URL = {} ", post.getPostId(),postImageURL);
        
        // 프론트로 보내주기 위해 Entitiy -> DTO로 변환
        PostToFrontDto postDTO = Post.convertPostDTO(post);
        postDTO.setFileName(postImageURL);

        // 게시글에 대한 댓글 작성자의 정보를 찾음
        List<ReplyToFrontDto> replies=postDTO.getReplies();
        Map<String, String> repliersProfile = new HashMap<>();
        List<Map<String,String>> repliersProfileName=new ArrayList<>();
        for(ReplyToFrontDto reply:replies){
            Long replierId=reply.getUserId();
            User replier=userService.findUserById(replierId);
            String replierProfile=pictureService.getProfileImage(replier);
            repliersProfile.put(replier.getLoginId(),replierProfile);
            repliersProfileName.add(pictureService.getProfileImageName(replier));
        }
        
        //게시글 작성자,댓글 작성자의 정보(아마존 S3 프로필 URL,File name을 보낼 데이터에 추가)
        PostDetailToFrontDto detail=new PostDetailToFrontDto(postDTO);
        detail.setWriterProfile(writerProfile);
        detail.setReplierProfile(repliersProfile);
        detail.setWriterRealFileName(writerProfileName);
        detail.setReplierRealFileName(repliersProfileName);


        DefaultResponse<PostDetailToFrontDto> defaultResponse = DefaultResponse.<PostDetailToFrontDto>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("게시물 상세 정보")
                .result(detail)
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }

    @PostMapping(value="/boardlist/write", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<? extends BasicResponse> boardWrite(
            //@RequestBody @Validated PostDto postDto,
            @RequestPart(value="PostDto") PostDto postDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart(value = "files") @Nullable List<MultipartFile> multipartFiles
    ) {
        log.info("작성한 게시물 정보:{}", postDto);

        User user = customUserDetails.getUser();

        Post post = postService.uploadPost(user, postDto);
        if(multipartFiles!=null){
            try {
                postFileService.uploadPostFile(multipartFiles, post, dirName);
            }catch (IOException e){
                log.info("사진 업로드 에러:{}", postDto);
            }
        }
        PostToFrontDto postDTO = Post.convertPostDTO(post);

        DefaultResponse<PostToFrontDto> defaultResponse = DefaultResponse.<PostToFrontDto>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("게시글 작성 완료")
                .result(postDTO)
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }

    @PatchMapping("/boardlist/edit/{postId}")
    public ResponseEntity<? extends BasicResponse> edit(
            @PathVariable Long postId,
            @RequestBody @Validated PostDto postDto
    ) {
        log.info("수정한 게시물 정보 = {}", postDto);

        postService.updatePost(postId, postDto);

        DefaultResponse<String> defaultResponse = DefaultResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("게시글 수정 완료")
                .result("")
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);

    }

    @DeleteMapping("/boardlist/delete/{postId}")
    public ResponseEntity<? extends BasicResponse> delete(@PathVariable("postId") Long postId) {
        //게시글 삭제
        log.info("삭제하려는 게시물 id : {}", postId);
        postFileService.deleteFilesByPostId(postId);
        postService.deletePost(postId);

        DefaultResponse<String> defaultResponse = DefaultResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("게시글 삭제 완료")
                .result("")
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }


    @PostMapping("/boardlist/search/{boardTitle}")
    public ResponseEntity<? extends BasicResponse> search(@PathVariable String boardTitle) {

        //게시글 검색 (제목으로)
        List<Post> posts = postService.searchByTitle(boardTitle);
        List<PostToFrontDto> postDTO = new ArrayList<>();

        for (Post post : posts) {
            postDTO.add(Post.convertPostDTO(post));
        }

        log.info("검색을 위해 입력한 단어={}", boardTitle);

        DefaultResponse<List<PostToFrontDto>> defaultResponse = DefaultResponse.<List<PostToFrontDto>>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("검색한 게시글들")
                .result(postDTO)
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }

    @PostMapping("/boardlist/{postId}/bookmark")
    public ResponseEntity<? extends BasicResponse> addBookmark(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long postId
    ) {
        User user = customUserDetails.getUser();

        Post post = postService.findPostById(postId);
        Bookmark bookmark = new Bookmark();
        bookmark.setPost(post);
        bookmark.setUser(user);
        bookmarkService.saveBookmark(bookmark);

        log.info("북마크 하려는 게시물ID ={} , 북마크ID ={}", post.getPostId(), bookmark.getBookmarkId());

        DefaultResponse<String> defaultResponse = DefaultResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("북마크 지정완료")
                .result("")
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }

    @GetMapping("/boardlist/best")
    public ResponseEntity<? extends BasicResponse> bestPost() {
        List<Post> posts = bookmarkService.getPostByPopular();
        List<PostToFrontDto> postDTO = new ArrayList<>();

        for (Post post : posts) {
            postDTO.add(Post.convertPostDTO(post));
        }

        log.info("북마크가 많이 된 순(조인횟수)의 게시글들 ={}", postDTO);

        DefaultResponse<List<PostToFrontDto>> defaultResponse = DefaultResponse.<List<PostToFrontDto>>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("북마크된 순 게시글들 가져오기 완료")
                .result(postDTO)
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }

    @GetMapping("/boardlist/user")
    public ResponseEntity<? extends BasicResponse> userPost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        List<Post> posts = postService.showMyPost(customUserDetails.getUser().getLoginId());
        List<PostToFrontDto> postDTO = new ArrayList<>();

        List<Reply> replies=replyService.findReplyByLoginId(customUserDetails.getUser().getLoginId());

        List<Bookmark> bookmarks=bookmarkService.getMyBookmark(customUserDetails.getUser().getId());
        for (Post post : posts) {
            postDTO.add(Post.convertPostDTO(post));
        }

        Map<String, Long> formData = new HashMap<>();
        formData.put("postNum",postDTO.stream().count());
        formData.put("replyNum",replies.stream().count());
        formData.put("bookmarkNum",bookmarks.stream().count());


        DefaultResponse<Map<String, Long>> defaultResponse = DefaultResponse.<Map<String, Long>>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("본인이 작성한 게시글 수,댓글 수")
                .result(formData)
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }

    @GetMapping("/boardlist/detail/{postId}/count")
    public ResponseEntity<? extends BasicResponse> postDetail(
            @PathVariable Long postId
    ){
        Post post=postService.findPostById(postId);
        Long replyNum=post.getReplies().stream().count();
        Long bookmarkNum=post.getBookmark().stream().count();

        log.info("게시글의 댓글 수 = {}, 북마크 수 = {}", replyNum,bookmarkNum);
        Map<String, Long> formData = new HashMap<>();
        formData.put("replyNum",replyNum);
        formData.put("bookmarkNum",bookmarkNum);

        DefaultResponse<Map<String, Long>> defaultResponse = DefaultResponse.<Map<String, Long>>builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("본인이 작성한 게시글 수,댓글 수")
                .result(formData)
                .build();

        return ResponseEntity.ok()
                .body(defaultResponse);
    }
}