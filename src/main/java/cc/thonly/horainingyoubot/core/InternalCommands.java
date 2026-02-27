package cc.thonly.horainingyoubot.core;

import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.command.internal.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InternalCommands {
    @Autowired
    CommandOp commandOp;
    @Autowired
    CommandDeop commandDeop;
    @Autowired
    CommandUser commandUser;
    @Autowired
    CommandGroup commandGroup;
    @Autowired
    CommandReload commandReload;
    @Autowired
    CommandEula commandEula;
    @Autowired
    CommandPlugins commandPlugins;
    @Autowired
    CommandTree commandTree;
    @Autowired
    CommandHelp commandHelp;
    @Autowired
    CommandOS commandOs;
    @Autowired
    CommandExecute commandExecute;
    @Autowired
    CommandAbout commandAbout;

    public void accept(Commands commands) {
        commands.registerCommand(this.commandOp);
        commands.registerCommand(this.commandDeop);
        commands.registerCommand(this.commandUser);
        commands.registerCommand(this.commandGroup);
        commands.registerCommand(this.commandReload);
        commands.registerCommand(this.commandEula);
        commands.registerCommand(this.commandPlugins);
        commands.registerCommand(this.commandTree);
        commands.registerCommand(this.commandHelp);
        commands.registerCommand(this.commandOs);
        commands.registerCommand(this.commandExecute);
        commands.registerCommand(this.commandAbout);
    }
}
