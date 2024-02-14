package com.deliveroo.rider.pojo.dto;

import com.deliveroo.rider.entity.Account;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@ToString
public class CustomUserDetails implements UserDetails {
    private Account account;

    public CustomUserDetails(Account account){
        this.account = account;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.account.getSecurityCode();
    }

    @Override
    public String getUsername() {
        return this.account.getFirstName() + " " + this.account.getLastName();
    }

    @Override
    public boolean isAccountNonExpired() {
        if(this.account.getExpirationDate() == null){
            return true;
        }else {
            return this.account.getExpirationDate().isAfter(LocalDateTime.now());
        }
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
}
