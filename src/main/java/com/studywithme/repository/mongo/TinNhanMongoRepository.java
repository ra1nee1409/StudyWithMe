package com.studywithme.repository.mongo;

import com.studywithme.chat.mongo.TinNhanMongo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TinNhanMongoRepository extends MongoRepository<TinNhanMongo, String> {

    List<TinNhanMongo> findByCuocTroChuyenIdOrderByThoiGianGuiAsc(String cuocTroChuyenId);

    Page<TinNhanMongo> findByCuocTroChuyenIdOrderByThoiGianGuiDesc(String cuocTroChuyenId, Pageable pageable);

    long countByCuocTroChuyenIdAndNguoiNhanIdAndDaDocFalse(String cuocTroChuyenId, Long nguoiNhanId);

    List<TinNhanMongo> findByCuocTroChuyenIdAndNguoiNhanIdAndDaDocFalse(String cuocTroChuyenId, Long nguoiNhanId);
}
