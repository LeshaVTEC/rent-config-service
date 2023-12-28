package rentconfigservice.httpclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import rentconfigservice.core.dto.audit.AuditDto;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(name = "audit-logs", url = "${custom.feign.audit-logs.url}/audit")
public interface AuditFeignClient {

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    AuditDto sendRequestToCreateLog(
            @RequestHeader String AUTHORIZATION,
            @RequestBody AuditDto auditDto
    );
}
