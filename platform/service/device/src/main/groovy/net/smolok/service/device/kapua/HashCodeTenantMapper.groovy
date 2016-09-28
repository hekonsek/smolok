package net.smolok.service.device.kapua

class HashCodeTenantMapper implements TenantMapper {

    @Override
    BigInteger tenantMapping(String tenant) {
        tenant.hashCode().toBigInteger()
    }

}
