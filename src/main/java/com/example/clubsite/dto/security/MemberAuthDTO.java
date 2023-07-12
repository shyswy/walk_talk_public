package com.example.clubsite.dto.security;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Log4j2
@Getter
@Setter
@ToString
public class MemberAuthDTO implements UserDetails {
    @ApiModelProperty(hidden = true)
    public static final String DEFAULT_PASSWORD = "1111";
    @ApiModelProperty(hidden = true)
    private Long id;
    @ApiModelProperty(hidden = true)
    private String password;
    @ApiModelProperty(hidden = true)
    private Collection<? extends GrantedAuthority> authorities;

    public MemberAuthDTO(Long id, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.password = DEFAULT_PASSWORD;
        this.authorities = authorities;
    }

    @Override
    @ApiModelProperty(hidden = true)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    @ApiModelProperty(hidden = true)
    public String getPassword() {
        return password;
    }

    @Override
    @ApiModelProperty(hidden = true)
    public String getUsername() {
        return "User" + this.id.toString();
    }

    @Override
    @ApiModelProperty(hidden = true)
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @ApiModelProperty(hidden = true)
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @ApiModelProperty(hidden = true)
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @ApiModelProperty(hidden = true)
    public boolean isEnabled() {
        return true;
    }
}
