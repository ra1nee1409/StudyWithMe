package com.studywithme.service;

import com.studywithme.dto.request.TienDoBaiHocRequest;
import com.studywithme.dto.response.PhongHocDto;
import com.studywithme.dto.response.TienDoKhoaHocResponse;

public interface StudentLearningService {

    /**
     * Lấy toàn bộ thông tin bài học, danh mục chương học và tiến độ cho phòng học LMS.
     * Ném ResourceNotFoundException nếu không tìm thấy khóa học, hoặc AccessDeniedException nếu học viên chưa ghi danh.
     */
    PhongHocDto layThongTinPhongHoc(String courseSlug, Long studentId, Long lessonId);

    /**
     * Đánh dấu hoặc hủy đánh dấu hoàn thành bài học, cập nhật % tiến độ khóa học.
     */
    TienDoKhoaHocResponse capNhatTienDo(Long studentId, TienDoBaiHocRequest request);

    /**
     * Tiện ích chuẩn hóa link video YouTube hoặc MP4 thành định dạng nhúng (embed) an toàn.
     */
    String formatVideoEmbedUrl(String rawUrl);
}

