package com.example.capstone_3.Controller;

import com.example.capstone_3.Api.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ImageUploadTests {
    @TempDir Path directory;
    @Test void savedImageSurvivesControllerRestart() throws Exception {
        var bytes = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB), "png", bytes);
        var result = new ImageUploadController(directory.toString()).upload(new MockMultipartFile("file", "../avatar.png", "image/png", bytes.toByteArray()));
        String url = (String) ((Map<?, ?>) result.getBody()).get("url");
        String name = url.substring(url.lastIndexOf('/') + 1);
        var read = new ImageUploadController(directory.toString()).read(name);
        assertEquals(200, read.getStatusCode().value());
        assertEquals("image/png", read.getHeaders().getContentType().toString());
        assertNotNull(ImageIO.read(new java.io.ByteArrayInputStream(read.getBody().getByteArray())));
    }
    @Test void renamedHtmlCannotBeUploadedAsAnImage() {
        assertThrows(ApiException.class, () -> new ImageUploadController(directory.toString()).upload(new MockMultipartFile("file", "avatar.png", "image/png", "<script>alert(1)</script>".getBytes())));
    }
    @Test void excessiveSizeIsRejected() {
        assertThrows(ApiException.class, () -> new ImageUploadController(directory.toString()).upload(new MockMultipartFile("file", "avatar.png", "image/png", new byte[2 * 1024 * 1024 + 1])));
    }
    @Test void filenamesCannotReadOutsideStorage() throws Exception {
        assertEquals(404, new ImageUploadController(directory.toString()).read("../application.properties").getStatusCode().value());
    }
}
