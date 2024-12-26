package com.stream.app.spring_stream_app.service;

import com.stream.app.spring_stream_app.model.Video;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface VideoService {
    //save video
     Video save(Video video, MultipartFile file);
     //get All video
     List<Video> findAll();
     //find by title
     Optional<Video> findByTitle(String title);
     //find by id
     Video findById(String id);
     void deleteVideoById(String id);
     Video streamVideo(String id);
     String processVideo(String id);
}
