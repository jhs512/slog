package com.ll.rsv.domain.base.genFile.entity.GenFile;

import com.ll.rsv.global.app.AppConfig;
import com.ll.rsv.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.io.File;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {
                        "relId", "relTypeCode", "typeCode", "type2Code", "fileNo"
                }
        ),
        indexes = {
                // 특정 그룹의 데이터들을 불러올 때
                @Index(name = "GenFile__idx2", columnList = "relTypeCode, typeCode, type2Code")
        }
)
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@Getter
@Setter
public class GenFile extends BaseTime {
    @Column(unique = true)
    private String fileName;
    private String relTypeCode;
    private long relId;
    private String typeCode;
    private String type2Code;
    private String fileExtTypeCode;
    private String fileExtType2Code;
    private long fileSize;
    private long fileNo;
    private String fileExt;
    private String fileDir;
    private String originFileName;

    public String getUrl() {
        return "/gen/" + getFileDir() + "/" + getFileName();
    }

    public String getDownloadUrl() {
        return "/genFile/download/" + getFileName();
    }

    public String getFilePath() {
        return AppConfig.getGenFileDirPath() + "/" + getFileDir() + "/" + getFileName();
    }

    public void deleteOnDisk() {
        String filePath = getFilePath();
        new File(filePath).delete();
    }
}
