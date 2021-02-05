package uwu.smsgamer.pasteclient.command.commands;

import uwu.smsgamer.pasteclient.PasteClient;
import uwu.smsgamer.pasteclient.command.*;
import uwu.smsgamer.pasteclient.fileSystem.FileManager;
import uwu.smsgamer.pasteclient.utils.ChatUtils;

import java.util.*;

public class ConfigCommand extends Command {

    public ConfigCommand() {
        super("config");
    }

    @Override
    public void run(String alias, String[] args) {
        switch (args.length) {
            case 0:
                ChatUtils.info(".config <list|show|save|load> [...]");
                break;
            case 1:
                switch (args[0].toLowerCase()) {
                    case "list":
                    case "show": {
                        list();
                        return;
                    }
                    case "save":
                    case "load":
                        ChatUtils.info(".config <" + args[0] + "> <config name>");
                        return;
                }
                ChatUtils.info(".config <list|show|save|load> [...]");
                break;
            default:
                switch (args[0].toLowerCase()) {
                    case "list":
                    case "show":
                        list();
                        return;
                    case "save":
                        try {
                            PasteClient.INSTANCE.fileManager.save(args[1]);
                            ChatUtils.success("Saved config.");
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new CommandException("Unable to save config.");
                        }
                        return;
                    case "load":
                        try {
                            PasteClient.INSTANCE.fileManager.load(args[1]);
                            ChatUtils.success("Saved config.");
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new CommandException("Unable to load config.");
                        }
                        ChatUtils.info(".config <" + args[0] + "> <config name>");
                        return;
                }
                ChatUtils.info(".config <list|show|save|load> [...]");
        }
    }

    public void list() {
        try {
            StringBuilder sb = new StringBuilder(ChatUtils.PRIMARY_COLOR)
              .append("Config Files ").append(ChatUtils.SECONDARY_COLOR).append(":§r");
            FileManager.configDir.mkdirs();
            for (String s : FileManager.configDir.list()) {
                sb.append(s).append(", ");
            }
            sb.setLength(sb.length() - 2);
            ChatUtils.send(sb.toString());
        } catch (Exception e) {
            throw new CommandException("Unable to find configs config.");
        }
    }

    @Override
    public List<String> autocomplete(int arg, String[] args) {
        return new ArrayList<>();
    }
}
