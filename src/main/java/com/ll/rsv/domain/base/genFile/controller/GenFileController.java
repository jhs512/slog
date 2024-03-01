package com.ll.rsv.domain.base.genFile.controller;

import com.ll.rsv.domain.base.genFile.entity.GenFile.GenFile;
import com.ll.rsv.domain.base.genFile.service.GenFileService.GenFileService;
import com.ll.rsv.global.exceptions.GlobalException;
import com.ll.rsv.standard.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Controller
@RequestMapping("/genFile")
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Tag(name = "GenFileController", description = "파일 다운로드 등 다양한 기능 제공")
public class GenFileController {
    private final GenFileService genFileService;

    @GetMapping("/download/{fileName}")
    @Operation(summary = "파일 다운로드")
    public ResponseEntity<Resource> download(
            @PathVariable String fileName, HttpServletRequest request
    ) throws FileNotFoundException {
        GenFile genFile = genFileService.findByFileName(fileName).orElseThrow(
                GlobalException.E404::new
        );
        String filePath = genFile.getFilePath();

        Resource resource = new InputStreamResource(new FileInputStream(filePath));

        String contentType = request.getServletContext().getMimeType(new File(filePath).getAbsolutePath());

        if (contentType == null) contentType = "application/octet-stream";

        String downloadFileName = Ut.url.encode(genFile.getOriginFileName()).replace("%20", " ");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadFileName + "\"")
                .contentType(MediaType.parseMediaType(contentType)).body(resource);
    }
}
