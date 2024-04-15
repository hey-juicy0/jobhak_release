package univcapstone.employmentsite.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import univcapstone.employmentsite.domain.User;
import univcapstone.employmentsite.repository.UserRepository;

import java.util.Collections;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        return userRepository.findByLoginId(loginId)
                .map(this::createUser)
                .orElseThrow(() ->  new UsernameNotFoundException(loginId + "을(를) 찾을 수 없습니다."));
    }

    private CustomUserDetails createUser(User user) {
        return new CustomUserDetails(user,
                Collections.singleton(new SimpleGrantedAuthority(user.getAuthority().getRole())));
    }
}
