package com.studywithme.entity;

import com.studywithme.entity.enums.shared.VaiTroName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vai_tro")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaiTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "ten_vai_tro", nullable = false, unique = true, length = 50)
    private VaiTroName tenVaiTro;

    @Column(name = "mo_ta", length = 255)
    private String moTa;
}
