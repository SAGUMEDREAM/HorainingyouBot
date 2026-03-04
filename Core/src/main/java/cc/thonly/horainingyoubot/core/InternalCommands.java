package cc.thonly.horainingyoubot.core;

import cc.thonly.horainingyoubot.command.Commands;
import cc.thonly.horainingyoubot.command.internal.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InternalCommands {
    @Autowired
    CommandAbout commandAbout;
    @Autowired
    CommandCopyGroupMembers commandCopyGroupMembers;
    @Autowired
    CommandDeop commandDeop;
    @Autowired
    CommandEula commandEula;
    @Autowired
    CommandExecute commandExecute;
    @Autowired
    CommandGroup commandGroup;
    @Autowired
    CommandHelp commandHelp;
    @Autowired
    CommandKick commandKick;
    @Autowired
    CommandMute commandMute;
    @Autowired
    CommandOp commandOp;
    @Autowired
    CommandOS commandOs;
    @Autowired
    CommandPlugins commandPlugins;
    @Autowired
    CommandReload commandReload;
    @Autowired
    CommandTree commandTree;
    @Autowired
    CommandUnmute commandUnmute;
    @Autowired
    CommandUser commandUser;
    @Autowired
    CommandWholeMute commandWholeMute;

    public void accept(Commands commands) {
        commands.registerCommand(this.commandAbout);
        commands.registerCommand(this.commandCopyGroupMembers);
        commands.registerCommand(this.commandDeop);
        commands.registerCommand(this.commandEula);
        commands.registerCommand(this.commandExecute);
        commands.registerCommand(this.commandGroup);
        commands.registerCommand(this.commandHelp);
        commands.registerCommand(this.commandKick);
        commands.registerCommand(this.commandMute);
        commands.registerCommand(this.commandOp);
        commands.registerCommand(this.commandOs);
        commands.registerCommand(this.commandPlugins);
        commands.registerCommand(this.commandReload);
        commands.registerCommand(this.commandTree);
        commands.registerCommand(this.commandUnmute);
        commands.registerCommand(this.commandUser);
        commands.registerCommand(this.commandWholeMute);
    }
}
