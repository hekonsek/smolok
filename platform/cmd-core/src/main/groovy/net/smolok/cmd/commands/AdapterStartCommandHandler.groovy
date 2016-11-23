package net.smolok.cmd.commands

import net.smolok.paas.Paas

class AdapterStartCommandHandler extends ServiceStartCommandHandler {

    AdapterStartCommandHandler(Paas paas) {
        super(paas)
    }

    @Override
    protected String[] commandPrefix() {
        ['adapter-start']
    }

}