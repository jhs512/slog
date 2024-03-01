package com.ll.rsv.domain.wikenMigrate.wikenMigrate.controller;


import com.ll.rsv.domain.wikenMigrate.wikenMigrate.service.WikenMigrateService;
import com.ll.rsv.global.rq.Rq;
import com.ll.rsv.global.rsData.RsData;
import com.ll.rsv.standard.base.Empty;
import com.ll.rsv.standard.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping(value = "/api/v1/wikenMigrate", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@Tag(name = "ApiV1WikenMigrateController", description = "위캔 마이그레이트 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiV1WikenMigrateController {
    private final WikenMigrateService wikenMigrateService;
    private final Rq rq;


    public record MigrateRequestBody(String username, String password) {
    }

    @PostMapping(value = "/migrate")
    @Operation(summary = "마이그레이트")
    @Transactional
    public RsData<Empty> migrate(@Valid @RequestBody MigrateRequestBody body) {
        return wikenMigrateService.migrate(
                rq.getMember(),
                Ut.str.hasLength(body.username) ? body.username : rq.getMember().getUsername(),
                body.password
        );
    }
}
