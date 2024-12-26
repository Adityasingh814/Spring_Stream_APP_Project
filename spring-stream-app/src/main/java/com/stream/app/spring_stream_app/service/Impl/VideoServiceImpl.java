package com.stream.app.spring_stream_app.service.Impl;

import com.stream.app.spring_stream_app.model.Video;
import com.stream.app.spring_stream_app.repo.VideoRepository;
import com.stream.app.spring_stream_app.service.VideoService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
   private VideoRepository videoRepository;
    @Value("${files.video}")
    String DIR;
    @Value("${files.video.hsl}")
    String HSL_DIR;
    @PostConstruct
    public void init(){
        File file = new File(DIR);
        File file1 = new File(HSL_DIR);
        if(!file1.exists()){
            file1.mkdir();
            System.out.println("HLS created");
        }
        if(!file.exists()){
            file.mkdir();
            System.out.println("Folder is created successfully");
        }else{
            System.out.println("Folder is already existed");
        }
    }
    @Override
    public Video save(Video video, MultipartFile file) {
        try {
            String filename = file.getOriginalFilename();
            String contentType = file.getContentType();
            InputStream inputStream = file.getInputStream();

            //folder path create
            String cleanFolder = StringUtils.cleanPath(DIR);
            String cleanfileName = StringUtils.cleanPath(filename);

            //folder path with filename
            Path path =  Paths.get(cleanFolder,cleanfileName);
            System.out.println(path);
            System.out.println(contentType);

            //copy file to the folder
            Files.copy(inputStream,path, StandardCopyOption.REPLACE_EXISTING);

            //video meta data
           video.setContentType(contentType);
           video.setFilePath(path.toString());
          Video savedVideo =   videoRepository.save(video);
            processVideo(savedVideo.getId());
            //meta data save to database
            return savedVideo;
        } catch (IOException e) {
             e.printStackTrace();
             return null;
        }
    }

    @Override
    public List<Video> findAll() {
        return videoRepository.findAll();
    }

    @Override
    public Optional<Video> findByTitle(String title) {
        return videoRepository.findByTitle(title);
    }

    @Override
    public Video findById(String id) {
        return videoRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteVideoById(String id) {
        videoRepository.deleteById(id);
    }

    @Override
    public Video streamVideo(String id) {
      Video video =  videoRepository.findById(id).orElseThrow(()->new RuntimeException("Video not found"));
         return video;
    }

    @Override
    public String processVideo(String id) {
        Video video = this.findById(id);
        String filepath = video.getFilePath();
        Path videoPath = Paths.get(filepath);
        try{
            //ffmpeg command
            Path outputPath = Paths.get(HSL_DIR,id);
            Files.createDirectories(outputPath);
            String ffmpegCMD = String.format(
                    "ffmpeg -i \"%s\" -c:v libx264 -c:a aac -strict -2 -f hls -hls_time 10 -hls_list_size 0 -hls_segment_filename \"%s/segment_%%3d.ts\"  \"%s/master.m3u8\" ",
                    videoPath, outputPath, outputPath
            );
            System.out.println(ffmpegCMD);
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", ffmpegCMD);
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            int exit = process.waitFor();
            if (exit != 0) {
                throw new RuntimeException("video processing failed!!");
            }
            return id;

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
