package com.hrms.hrms_backend.repository;

import com.hrms.hrms_backend.model.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByTargetAudienceInOrderByCreatedAtDesc(List<String> audiences);
}
