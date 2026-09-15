package com.iot.core.tenant;

import java.security.SecureRandom;
import java.util.Locale;

//默认租户号生成器
public class IotTenantId implements TenantId {

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generate() {

        int num = secureRandom.nextInt(999_999)+1;

        return String.format(Locale.ROOT, "%06d", num);
    }
}
