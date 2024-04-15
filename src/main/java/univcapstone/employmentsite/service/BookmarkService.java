package univcapstone.employmentsite.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import univcapstone.employmentsite.domain.Bookmark;
import univcapstone.employmentsite.domain.Post;
import univcapstone.employmentsite.repository.BookmarkRepository;

import java.util.List;

@Slf4j
@Transactional
@Service
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;

    @Autowired
    public BookmarkService(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }


    public void saveBookmark(Bookmark bookmark){
        bookmarkRepository.save(bookmark);
    }

    public List<Bookmark> getMyBookmark(Long userId){
        return bookmarkRepository.getMyBookmark(userId);
    }

    public void deleteBookmark(Long bookmarkId){
        Bookmark bookmark=bookmarkRepository.findByBookmarkId(bookmarkId);
        if(bookmark==null){
            throw new IllegalStateException("해당 북마크는 존재하지 않습니다.");
        }
        bookmarkRepository.delete(bookmark);

    }

    public List<Post> getPostByPopular() {
        return bookmarkRepository.getAllPostByPopular();
    }
}
