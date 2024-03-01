package com.ll.rsv.domain.base.genFile.dto;

import com.ll.rsv.domain.base.genFile.entity.GenFile.GenFile;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Getter
public class GenFileDto {
    @NonNull
    private Long id;
    @NonNull
    private LocalDateTime createDate;
    @NonNull
    private LocalDateTime modifyDate;
    @NonNull
    private String fileName;
    @NonNull
    private String relTypeCode;
    @NonNull
    private long relId;
    @NonNull
    private String typeCode;
    @NonNull
    private String type2Code;
    @NonNull
    private String fileExtTypeCode;
    @NonNull
    private String fileExtType2Code;
    @NonNull
    private long fileSize;
    @NonNull
    private long fileNo;
    @NonNull
    private String fileExt;
    @NonNull
    private String fileDir;
    @NonNull
    private String originFileName;
    @NonNull
    private String url;
    @NonNull
    private String downloadUrl;

    public GenFileDto(GenFile genFile) {
        this.id = genFile.getId();
        this.createDate = genFile.getCreateDate();
        this.modifyDate = genFile.getModifyDate();
        this.fileName = genFile.getFileName();
        this.relTypeCode = genFile.getRelTypeCode();
        this.relId = genFile.getRelId();
        this.typeCode = genFile.getTypeCode();
        this.type2Code = genFile.getType2Code();
        this.fileExtTypeCode = genFile.getFileExtTypeCode();
        this.fileExtType2Code = genFile.getFileExtType2Code();
        this.fileSize = genFile.getFileSize();
        this.fileNo = genFile.getFileNo();
        this.fileExt = genFile.getFileExt();
        this.fileDir = genFile.getFileDir();
        this.originFileName = genFile.getOriginFileName();
        this.url = genFile.getUrl();
        this.downloadUrl = genFile.getDownloadUrl();
    }
}
