package net.smolok.cmd.commands

import net.smolok.paas.Paas

class AdapterStartCommand extends ServiceStartCommand {

    AdapterStartCommand(Paas paas) {
        super(paas)
    }

    @Override
    protected String[] commandPrefix() {
        ['adapter-start']
    }

}