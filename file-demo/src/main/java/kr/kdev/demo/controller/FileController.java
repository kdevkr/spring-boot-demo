package kr.kdev.demo.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedOutputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@CrossOrigin(exposedHeaders = {HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS})
@RestController
@RequestMapping("/files")
public class FileController {

    @GetMapping("/sample.png")
    public ResponseEntity<StreamingResponseBody> image() throws MalformedURLException {
        Resource resource = new UrlResource("https://sample-videos.com/img/Sample-png-image-100kb.png");
        StreamingResponseBody responseBody = output ->
                StreamUtils.copy(resource.getInputStream(), new BufferedOutputStream(output)); // NOTE: 8192 bytes.
        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename("sample.png", StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }


    @GetMapping("/sample.mp4")
    public ResponseEntity<List<ResourceRegion>> streamingVideo(@RequestHeader HttpHeaders headers) throws MalformedURLException {
        Resource resource = new UrlResource("https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_30mb.mp4");
        List<ResourceRegion> resourceRegions = HttpRange.toResourceRegions(headers.getRange(), resource);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .contentType(MediaType.parseMediaType("video/mp4"))
                .body(resourceRegions);
    }

    @GetMapping("/sample.stream")
    public ResponseEntity<Resource> streamingVideo() throws MalformedURLException {
        Resource resource = new UrlResource("https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_30mb.mp4");
        return ResponseEntity.ok().body(resource);
    }
}