package com.stream.app.spring_stream_app.controller;

import com.stream.app.spring_stream_app.AppConstants;
import com.stream.app.spring_stream_app.model.Video;
import com.stream.app.spring_stream_app.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin("*")

public class VideoController{
    @Autowired
    private VideoService videoService;

    @PostMapping
    public ResponseEntity<?> saveVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(required = false )String contentType
    ){
        Video video = new Video();
        video.setId(UUID.randomUUID().toString());
        video.setTitle(title);
        video.setDescription(description);
        video.setContentType(contentType);
        System.out.println(video);
        System.out.println(file);
        System.out.println(title);
        System.out.println(description);
        System.out.println(contentType);

        Video savedVideo =  videoService.save(video,file);
        if(savedVideo != null){
            return ResponseEntity.status(HttpStatus.OK).body(video);
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Video not saved");
        }
    }

    @GetMapping
    public List<Video> getAllVideos(){
        return videoService.findAll();
    }

    //stream video
    @GetMapping("/stream/{id}")
    public ResponseEntity<Resource> stream(@PathVariable String id) {
        Video video = videoService.streamVideo(id);
        String filepath = video.getFilePath();
        String contentType = video.getContentType();
        if(contentType == null) {
            contentType = "application/octet-stream";
        }
        Resource resource = new FileSystemResource(filepath);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
    }

   //stream videos in chunks
    @GetMapping("/stream/range/{id}")
    public ResponseEntity<Resource> streamInRange(
          @PathVariable String id,
          @RequestHeader(value = "Range", required = false) String range
          ){
        System.out.println(range);
        Video video =   videoService.streamVideo(id);
        Path path =  Paths.get(video.getFilePath());
        Resource resource  = new FileSystemResource(path);
        String contentType = video.getContentType();
        if(contentType == null) {
            contentType = "application/octet-stream";
        }
        long fileLength = path.toFile().length();
        if(range == null) {
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
        }
        long rangeStart;
        long rangeEnd;
       String[] ranges = range.replace("bytes=","").split("-");
       rangeStart = Long.parseLong(ranges[0]);
       rangeEnd = rangeStart+ AppConstants.CHUNK_SIZE-1;
       if(rangeEnd>=fileLength) {
          rangeEnd = fileLength - 1;
      }
        System.out.println("Range Start"+rangeStart);
        System.out.println("Range End"+rangeEnd);
        InputStream inputStream;
       try{
         inputStream = Files.newInputStream(path);
         inputStream.skip(rangeStart);
           long contentLength = rangeEnd-rangeStart+1;
           byte[] data = new byte[(int)contentLength];
          int read = inputStream.read(data,0,data.length);
           System.out.println("read in bytes : "+read);

           HttpHeaders headers = new HttpHeaders();
           headers.setContentLength(contentLength);
           headers.add("Content-Range","bytes "+rangeStart+"-"+rangeEnd+"/"+fileLength);
           headers.add("Cache-Control","no-cache,no-store,must-revalidate");
           headers.add("Pragma","no-cache");
           headers.add("Expires","0");
           headers.add("X-Content-Type-Options", "nosniff");
           return ResponseEntity
                   .status(HttpStatus.PARTIAL_CONTENT)
                   .headers(headers)
                   .contentType(MediaType.parseMediaType(contentType))
                   .body(new ByteArrayResource(data));
       } catch (IOException e) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
       }
    }

    @Value("${files.video.hsl}")
    private  String HSL_DIR;

    @GetMapping("/{id}/master.m3u8")
    public ResponseEntity<Resource>serverMasterFile(@PathVariable String id){
        Path path = Paths.get(HSL_DIR, id, "master.m3u8");
        System.out.println(path);
        if (!Files.exists(path))
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Resource resource = new FileSystemResource(path);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.apple.mpegurl")
                .body(resource);
    }

    @GetMapping("/{id}/{segment}.ts")
    public ResponseEntity<Resource> serveSegments(@PathVariable String id, @PathVariable String segment){
        Path path = Paths.get(HSL_DIR, id, segment+".ts");
        System.out.println(path);
        if(!Files.exists(path))
        {
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Resource resource = new FileSystemResource(path);

        return  ResponseEntity
        .ok()
        .header(HttpHeaders.CONTENT_TYPE, "video/mp2t").body(resource);
    }
}

