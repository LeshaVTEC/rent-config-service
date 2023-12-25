package rentconfigservice.aop;

import rentconfigservice.core.entity.AuditedAction;
import rentconfigservice.core.entity.EssenceType;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {

    AuditedAction auditedAction();

    EssenceType essenceType();
}
