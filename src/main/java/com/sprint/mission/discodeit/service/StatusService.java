//package com.sprint.mission.discodeit.service;
//
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//public interface StatusService<T> {
//
//  T create(Object request);
//
//  T find(UUID id);
//
//  default Map<UUID, T> findAllByUserId(UUID userId) {
//    return null;
//  }
//
//  default Map<UUID, UserReadStatusResponse> findAllByUserId(String userId) {
//    return null;
//  }
//
//  default Map<UUID, T> findAll() {
//    return null;
//  }
//
//  T update(Object request);
//
//  void delete(UUID id);
//
//  default List<ReadStatusResponse> findByUserId(UUID userId) {
//    return null;
//  }
//
//  default ReadStatusCreateResponse createReadStatus(ReadStatusRequest request) {
//    return null;
//  }
//
//  default ReadStatusModifyResponse updateReadStatus(UUID readStatusId,
//      ReadStatusModifyRequest request) {
//    return null;
//  }
//}
