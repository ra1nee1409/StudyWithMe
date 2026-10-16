package com.studywithme.config;

import com.studywithme.entity.NguoiDung;
import com.studywithme.entity.VaiTro;
import com.studywithme.entity.enums.TrangThaiNguoiDung;
import com.studywithme.entity.enums.VaiTroName;
import com.studywithme.repository.NguoiDungRepository;
import com.studywithme.repository.VaiTroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final VaiTroRepository vaiTroRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(VaiTroRepository vaiTroRepository,
                      NguoiDungRepository nguoiDungRepository,
                      PasswordEncoder passwordEncoder) {
        this.vaiTroRepository = vaiTroRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Khởi tạo 3 vai trò cơ bản nếu chưa tồn tại
        VaiTro roleQuanTri = initRole(VaiTroName.ROLE_QUAN_TRI, "Quản trị viên hệ thống");
        initRole(VaiTroName.ROLE_GIANG_VIEN, "Giảng viên / Gia sư trực tuyến");
        initRole(VaiTroName.ROLE_HOC_VIEN, "Học viên nền tảng");

        // 2. Khởi tạo tài khoản Admin mặc định nếu chưa tồn tại
        String adminEmail = "admin@studywithme.vn";
        if (!nguoiDungRepository.existsByEmail(adminEmail)) {
            NguoiDung admin = NguoiDung.builder()
                    .email(adminEmail)
                    .matKhau(passwordEncoder.encode("Admin@123456"))
                    .hoTen("Quản Trị Viên Hệ Thống")
                    .soDienThoai("0987654321")
                    .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                    .danhSachVaiTro(Set.of(roleQuanTri))
                    .build();

            nguoiDungRepository.save(admin);
            logger.info(">>> Đã khởi tạo tài khoản Quản trị viên mặc định: {} / Admin@123456", adminEmail);
        }
    }

    private VaiTro initRole(VaiTroName vaiTroName, String moTa) {
        return vaiTroRepository.findByTenVaiTro(vaiTroName).orElseGet(() -> {
            VaiTro vaiTro = VaiTro.builder()
                    .tenVaiTro(vaiTroName)
                    .moTa(moTa)
                    .build();
            logger.info(">>> Đã khởi tạo vai trò: {}", vaiTroName);
            return vaiTroRepository.save(vaiTro);
        });
    }
}
