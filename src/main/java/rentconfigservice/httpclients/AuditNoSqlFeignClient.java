package rentconfigservice.httpclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import rentconfigservice.core.dto.audit.AuditDto;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(name = "audit-logs-no-sql", url = "${custom.feign.audit-logs-no-sql.url}/audit_no_sql")
public interface AuditNoSqlFeignClient {

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    AuditDto sendRequestToCreateLogNoSql(
            @RequestHeader String AUTHORIZATION,
            @RequestBody AuditDto auditDto
    );
}
