package com.studywithme.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cau_hinh_he_thong")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CauHinhHeThong {

    @Id
    @Column(name = "ma_cau_hinh", nullable = false, length = 100)
    private String maCauHinh;

    @Column(name = "gia_tri_cau_hinh", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String giaTriCauHinh;

    @Column(name = "mo_ta", length = 255)
    private String moTa;

    public String getGiaTri() {
        return giaTriCauHinh;
    }

    public void setGiaTri(String giaTri) {
        this.giaTriCauHinh = giaTri;
    }
}


