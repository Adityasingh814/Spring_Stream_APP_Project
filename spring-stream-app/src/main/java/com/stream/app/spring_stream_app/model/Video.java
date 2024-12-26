package com.stream.app.spring_stream_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CollectionIdJdbcTypeCode;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name ="youtube_video")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Video {
        @Id
        private String id;
        private String title;
        private String description;
        private String contentType;
        private String filePath;


    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
//   @ManyToOne
//   private Course course;
}
