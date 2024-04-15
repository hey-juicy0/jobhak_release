package univcapstone.employmentsite.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import univcapstone.employmentsite.domain.Post;
import univcapstone.employmentsite.domain.PostFile;
import univcapstone.employmentsite.repository.PostFileRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class PostFileService {
    private final AmazonS3 amazonS3;
    private final PostFileRepository postFileRepository;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public List<String> uploadPostFile(List<MultipartFile> multipartFiles, Post post, String dirName) throws IOException {
        if (multipartFiles.isEmpty()) {
            log.error("업로드한 파일이 존재하지 않습니다. 파일 생략");
            throw new FileNotFoundException("업로드한 파일이 존재하지 않습니다.");
        }

        //사용자가 업로드한 파일들의 URI를 저장하는 자료구조
        List<String> imagePathList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {

            File file = convertToFile(multipartFile);

            //S3에 올릴 파일명(UUID 이용)
            String uploadFilename = dirName + UUID.randomUUID() + file.getName();
            log.info("uploadFilename = {}", uploadFilename);

            //S3에 업로드
            String imagePath = uploadS3Post(uploadFilename, file,post);
            imagePathList.add(imagePath);

            //S3 업로드 후 로컬에 저장된 사진 삭제
            removeCreatedFile(file);
        }

        return imagePathList;
    }
    private String uploadS3Post(String uploadFilename, File file,Post post) {
        amazonS3.putObject(new PutObjectRequest(bucket, uploadFilename, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        log.info("uploadFilename = {}", uploadFilename);

        String imagePath = amazonS3.getUrl(bucket, uploadFilename).toString(); // 접근가능한 URL 가져오기

        PostFile picture = PostFile.builder()
                .uploadFileName(uploadFilename)
                .storeFileName(file.getName())
                .post(post)
                .build();

        postFileRepository.save(picture);
        return imagePath;
    }

    public Map<String,String> findFileByPostId(Long postId) {
        List<PostFile> files = postFileRepository.findAllByPostId(postId);
        Map<String,String> imageURL=new HashMap<>();
        for(PostFile file : files){
            URL url = amazonS3.getUrl(bucket,file.getUploadFileName());
            imageURL.put(url.toString(),file.getStoreFileName());
        }
        return imageURL;
    }

    public void deleteFilesByPostId(Long postId) {
        List<PostFile> postFiles=postFileRepository.findAllByPostId(postId);
        for(PostFile postFile : postFiles){
            log.info("삭제한 파일 이름 = {}",postFile.getUploadFileName());
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, postFile.getUploadFileName()));
        }
    }
    private File convertToFile(MultipartFile multipartFile) throws IOException {
        //확장자를 포함한 파일 이름을 가져온다.
        String filename = multipartFile.getOriginalFilename();
        log.info("filename = {}", filename);

        File file = new File("src/main/resources/temp/" + filename);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(multipartFile.getBytes());

        return file;
    }

    //로컬에 저장된 사진 삭제
    private void removeCreatedFile(File createdFile) {
        if (createdFile.delete()) {
            log.info("Created File delete success");
            return;
        }

        log.info("Created File delete fail");
    }


}
