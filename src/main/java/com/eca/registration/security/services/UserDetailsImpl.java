package com.eca.registration.security.services;

import com.eca.registration.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;

  private Long id;

  private String username;

  private String email;

  @JsonIgnore
  private String password;

  private List<GrantedAuthority> authorities;

  public UserDetailsImpl(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.email = user.getEmail();
    this.password = user.getPassword();
    this.authorities = Arrays.stream(user.getRoles().toString().split(","))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
  }
//
//  public static UserDetailsImpl build(User user) {
//    List<GrantedAuthority> authorities = user.getRoles().stream()
//        .map(role -> new SimpleGrantedAuthority(role.name()))
//        .collect(Collectors.toList());
//
//    return new UserDetailsImpl(
//        user.getId(),
//        user.getUsername(),
//        user.getEmail(),
//        user.getPassword(),
//        authorities);
//  }

  @Override
  public List<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
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
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
  }
}
