package com.studywithme.repository.mongo;

import com.studywithme.chat.mongo.CuocTroChuyenMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuocTroChuyenMongoRepository extends MongoRepository<CuocTroChuyenMongo, String> {

    @Query("{ $or: [ { 'nguoiDung1Id': ?0 }, { 'nguoiDung2Id': ?0 } ] }")
    List<CuocTroChuyenMongo> findAllByUserId(Long userId);

    @Query("{ $or: [ { 'nguoiDung1Id': ?0, 'nguoiDung2Id': ?1 }, { 'nguoiDung1Id': ?1, 'nguoiDung2Id': ?0 } ] }")
    Optional<CuocTroChuyenMongo> findConversationBetweenUsers(Long u1, Long u2);
}
