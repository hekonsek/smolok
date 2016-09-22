package org.eclipse.kapua.service.device.registry.mongodb

import net.smolok.service.documentstore.api.QueryBuilder
import org.eclipse.kapua.service.device.registry.DeviceQueryImpl
import org.eclipse.kapua.service.device.registry.KapuaId

class DocumentStoreDeviceQuery extends DeviceQueryImpl {

    private final QueryBuilder queryBuilder

    DocumentStoreDeviceQuery(KapuaId scopeId, QueryBuilder queryBuilder) {
        super(scopeId)
        this.queryBuilder = queryBuilder
    }

    QueryBuilder queryBuilder() {
        queryBuilder
    }

}
