package com.example.capstone_3.Controller;

import com.example.capstone_3.Api.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/media/images")
public class ImageUploadController {
    private final Path directory;
    public ImageUploadController(@Value("${app.upload-dir:uploads}") String directory) {
        this.directory = Path.of(directory).toAbsolutePath().normalize();
    }

    @PostMapping("")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty() || file.getSize() > 2 * 1024 * 1024)
            throw new ApiException("Choose a JPG or PNG image no larger than 2 MB");
        BufferedImage image;
        String format;
        try (var stream = ImageIO.createImageInputStream(new ByteArrayInputStream(file.getBytes()))) {
            if (stream == null) throw new ApiException("Choose a valid JPG or PNG image");
            var readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) throw new ApiException("Choose a valid JPG or PNG image");
            ImageReader reader = readers.next();
            try {
                format = reader.getFormatName().toLowerCase(Locale.ROOT);
                if (!List.of("jpeg", "jpg", "png").contains(format))
                    throw new ApiException("Only JPG and PNG images are supported");
                reader.setInput(stream);
                if ((long) reader.getWidth(0) * reader.getHeight(0) > 10_000_000)
                    throw new ApiException("Image dimensions must not exceed 10 million pixels");
                image = reader.read(0);
            } finally { reader.dispose(); }
        } catch (javax.imageio.IIOException error) {
            throw new ApiException("Choose a valid JPG or PNG image");
        }
        if (image == null) throw new ApiException("Choose a valid JPG or PNG image");
        String extension = format.equals("png") ? "png" : "jpg";
        String name = UUID.randomUUID() + "." + extension;
        Files.createDirectories(directory);
        Path destination = directory.resolve(name);
        // Re-encode decoded pixels instead of serving arbitrary uploaded bytes or metadata.
        try (OutputStream output = Files.newOutputStream(destination, StandardOpenOption.CREATE_NEW)) {
            if (!ImageIO.write(image, extension, output)) throw new IOException("Unable to encode image");
        }
        return ResponseEntity.ok(Map.of("url", "/api/v1/media/images/" + name));
    }

    @GetMapping("/{name}")
    public ResponseEntity<ByteArrayResource> read(@PathVariable String name) throws IOException {
        if (!name.matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}\\.(png|jpg)"))
            return ResponseEntity.notFound().build();
        Path file = directory.resolve(name).normalize();
        if (!file.getParent().equals(directory) || !Files.isRegularFile(file)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().contentType(name.endsWith(".png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG)
                .header("X-Content-Type-Options", "nosniff")
                .cacheControl(CacheControl.maxAge(java.time.Duration.ofDays(30)).cachePublic().immutable())
                .body(new ByteArrayResource(Files.readAllBytes(file)));
    }
}
