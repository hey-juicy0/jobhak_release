package univcapstone.employmentsite.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import univcapstone.employmentsite.domain.*;
import univcapstone.employmentsite.repository.PictureRepository;

import java.io.*;
import java.net.URL;
import java.util.*;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class PictureService {

    private final AmazonS3 amazonS3;
    private final PictureRepository pictureRepository;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public String uploadProfileFile(MultipartFile multipartFile, String dirName,User user) throws IOException {
        if (multipartFile.isEmpty()) {
            log.error("업로드 할 파일이 존재하지 않습니다.");
            throw new FileNotFoundException("업로드 할 파일이 존재하지 않습니다.");
        }

        //사용자가 업로드한 파일들의 URI를 저장하는 자료구조
        File file = convertToFile(multipartFile);

        //S3에 올릴 파일명(UUID 이용)
        String uploadFilename = dirName + UUID.randomUUID() + file.getName();
        log.info("uploadFilename = {}", uploadFilename);

        //S3에 업로드
        String imagePath = uploadS3Profile(uploadFilename, file,user);

        //S3 업로드 후 로컬에 저장된 사진 삭제
        removeCreatedFile(file);
        return imagePath;
    }
    private String uploadS3Profile(String uploadFilename, File file,User user) {
        log.info("uploadFilename = {}", uploadFilename);

        amazonS3.putObject(new PutObjectRequest(bucket, uploadFilename, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        String imagePath = amazonS3.getUrl(bucket, uploadFilename).toString(); // 접근가능한 URL 가져오기

        Picture picture = Picture.builder()
                .uploadFileName(uploadFilename)
                .storeFileName(file.getName())
                .isProfile(true)
                .user(user)
                .build();
        List<Picture> formerProfiles=pictureRepository.findAllByProfiles(user.getId());
        for(Picture profile : formerProfiles){
            deleteFile(profile);
        }
        pictureRepository.deleteFormerProfile(user.getId());
        pictureRepository.save(picture);
        return imagePath;
    }

    public String uploadConversionFile(MultipartFile multipartFile, String dirName,User user) throws IOException {
        if (multipartFile.isEmpty()) {
            log.error("업로드 할 파일이 존재하지 않습니다.");
            throw new FileNotFoundException("업로드 할 파일이 존재하지 않습니다.");
        }

        File file = convertToFile(multipartFile);

        String uploadFilename = dirName + UUID.randomUUID() + file.getName();
        log.info("uploadFilename = {}", uploadFilename);

        String imagePath = uploadS3ConverionFile(uploadFilename, file,user);

        removeCreatedFile(file);
        return imagePath;
    }
    private String uploadS3ConverionFile(String uploadFilename, File file,User user) {
        log.info("uploadFilename = {}", uploadFilename);

        amazonS3.putObject(new PutObjectRequest(bucket, uploadFilename, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        String imagePath = amazonS3.getUrl(bucket, uploadFilename).toString();

        Picture picture = Picture.builder()
                .uploadFileName(uploadFilename)
                .storeFileName(file.getName())
                .isProfile(false)
                .user(user)
                .build();
        pictureRepository.save(picture);
        return imagePath;
    }

    public Map<String,String> getProfileImageName(User user) {
        Picture picture=pictureRepository.findAllByProfile(user.getId());
        Map<String,String> imagesURL=new HashMap<>();
        if(picture==null){
            imagesURL.put("https://jobhakdasik2000-bucket.s3.ap-northeast-2.amazonaws.com/default/default.png",
                    "default.png");
            log.info("이미지가 없어서 default 이미지 전송");
            return imagesURL;
        }

        URL url = amazonS3.getUrl(bucket,picture.getUploadFileName());
        imagesURL.put(url.toString(),picture.getStoreFileName());

        return imagesURL;
    }
    public List<Picture> getAllProfileImageName(User user) {
        return pictureRepository.findAllByProfiles(user.getId());
    }
    public String getProfileImageOne(User user) {
        Picture picture=pictureRepository.findAllByProfile(user.getId());
        String imagesURL;
        if(picture==null){
            imagesURL="https://jobhakdasik2000-bucket.s3.ap-northeast-2.amazonaws.com/default/default.png";
            log.info("이미지가 없어서 default 이미지 전송");
            return imagesURL;
        }

        URL url = amazonS3.getUrl(bucket,picture.getUploadFileName());
        imagesURL=url.toString();
        return imagesURL;
    }
    public String getProfileImage(User user) {
        Picture picture=pictureRepository.findAllByProfile(user.getId());
        String imagesURL;
        if(picture==null){
            imagesURL="https://jobhakdasik2000-bucket.s3.ap-northeast-2.amazonaws.com/default/default.png";
            log.info("이미지가 없어서 default 이미지 전송");
            return imagesURL;
        }

        URL url = amazonS3.getUrl(bucket,picture.getUploadFileName());
        imagesURL=url.toString();

        return imagesURL;
    }
    public Picture getProfilePicture(User user) {
        Picture picture=pictureRepository.findAllByProfile(user.getId());
        if(picture==null){
            log.info("이미지가 없어서 default 이미지 전송");
            return null;
        }
        return picture;
    }
    public List<Map<String,String>> getConversionImage(User user) {
        List<Picture> pictures=pictureRepository.findAllByUserId(user.getId());

        List<Map<String,String>> imagesURL=new ArrayList<>();
        for(Picture picture : pictures){
            if(!picture.isProfile()){
                URL url = amazonS3.getUrl(bucket,picture.getUploadFileName());
                Map<String,String> data=new HashMap<>();
                data.put(url.toString(),picture.getStoreFileName());
                imagesURL.add(data);
            }
        }

        return imagesURL;
    }
    //MultipartFile to File
    //업로드할때 파일이 로컬에 없으면 에러가 발생하기 때문에 입력받은 파일을 로컬에 저장하고 업로드해야 함
    //따라서 S3에 업로드 이후 로컬에 저장된 사진을 삭제해야 함
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

    public String deleteFile(Picture picture) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, picture.getUploadFileName()));
        pictureRepository.delete(picture);
        return "success";
    }

}