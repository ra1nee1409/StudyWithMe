package com.studywithme.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.enums.shared.TrangThaiNguoiDung;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String hoTen;

    @JsonIgnore
    private final String matKhau;

    private final TrangThaiNguoiDung trangThai;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String email, String hoTen, String matKhau,
                         TrangThaiNguoiDung trangThai, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.hoTen = hoTen;
        this.matKhau = matKhau;
        this.trangThai = trangThai;
        this.authorities = authorities;
    }

    public static UserPrincipal create(NguoiDung nguoiDung) {
        List<GrantedAuthority> authorities = nguoiDung.getDanhSachVaiTro().stream()
                .map(vaiTro -> new SimpleGrantedAuthority(vaiTro.getTenVaiTro().name()))
                .collect(Collectors.toList());

        return new UserPrincipal(
                nguoiDung.getId(),
                nguoiDung.getEmail(),
                nguoiDung.getHoTen(),
                nguoiDung.getMatKhau(),
                nguoiDung.getTrangThai(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return matKhau;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return trangThai != TrangThaiNguoiDung.TAM_KHOA && trangThai != TrangThaiNguoiDung.BI_CAM;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return trangThai == TrangThaiNguoiDung.HOAT_DONG;
    }
}
