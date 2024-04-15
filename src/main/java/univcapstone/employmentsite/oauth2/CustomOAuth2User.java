package univcapstone.employmentsite.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import univcapstone.employmentsite.domain.Authority;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User, UserDetails {

    private final OAuth2UserInfo oAuth2UserInfo;

    public CustomOAuth2User(OAuth2UserInfo oAuth2UserInfo) {
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    @Override
    public String getPassword() {
        return null;
    }

    //OAuth2제공처의 이메일
    @Override
    public String getUsername() {
        return oAuth2UserInfo.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2UserInfo.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(Authority.USER.getRole()));
    }

    @Override
    public String getName() {
        return oAuth2UserInfo.getEmail();
    }

    public OAuth2UserInfo getUserInfo() {
        return oAuth2UserInfo;
    }
}
