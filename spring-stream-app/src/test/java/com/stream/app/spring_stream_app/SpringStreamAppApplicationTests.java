package com.stream.app.spring_stream_app;

import com.stream.app.spring_stream_app.service.Impl.VideoServiceImpl;
import com.stream.app.spring_stream_app.service.VideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringStreamAppApplicationTests {
@Autowired
VideoService videoService;
	@Test
	void contextLoads() {
		videoService.processVideo("ae9c3c18-c79d-4551-8ee0-ad08a9b00c7e");
	}

}
