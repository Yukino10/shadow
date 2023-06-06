package com.shadow.web.model.authority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;


public class JwtUser implements UserDetails {

	private static final long serialVersionUID = 1L;
	
	private final Integer id;
    private final String username; //用户登陆名login_name
    private final String password;
    private final Integer deleted;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Date createTime;
    private final Date lastLogoutTime;

    public JwtUser(
    		Integer id,
          String username,
          String password,
          Integer deleted,
          Collection<? extends GrantedAuthority> authorities,
          Date createTime,
          Date lastLogoutTime
    ) {
        this.id = id;
        this.username = username;
        this.deleted = deleted;
        this.password = password;
        this.authorities = authorities;
        this.createTime = createTime;
        this.lastLogoutTime = lastLogoutTime;
    }

    @JsonIgnore
    public Integer getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return deleted == 0;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @JsonIgnore
    public Date getlastLogoutTime() {
        return lastLogoutTime;
    }

    public Integer getState(){ return this.deleted; }

    public Date getCreateTime() {
        return createTime;
    }
}
