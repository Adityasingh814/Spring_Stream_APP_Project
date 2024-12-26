package com.stream.app.spring_stream_app.repo;

import com.stream.app.spring_stream_app.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video,String> {
    Optional<Video>findByTitle(String title);

}
