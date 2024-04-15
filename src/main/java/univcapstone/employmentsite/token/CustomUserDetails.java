package univcapstone.employmentsite.token;

import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import univcapstone.employmentsite.domain.User;

import java.util.Collection;

@Getter
@ToString
public class CustomUserDetails implements UserDetails {

    private User user;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getLoginId();
    }

    /* 계정 만료 여부
     * true :  만료 안됨
     * false : 만료
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /* 계정 잠김 여부
     * true : 잠기지 않음
     * false : 잠김
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /* 비밀번호 만료 여부
     * true : 만료 안 됨
     * false : 만료
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /* 사용자 활성화 여부
     * true : 활성화 됨
     * false : 활성화 안 됨
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
