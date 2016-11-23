package net.smolok.cmd.commands

import net.smolok.paas.Paas
import net.smolok.service.configuration.api.ConfigurationService

class AdapterStartCommandHandler extends ServiceStartCommandHandler {

    AdapterStartCommandHandler(Paas paas, ConfigurationService configurationService) {
        super(paas, configurationService)
    }

    @Override
    protected String[] commandPrefix() {
        ['adapter-start']
    }

}