package com.darkblade12.itemslotmachine.plugin.command;

import com.darkblade12.itemslotmachine.plugin.PluginBase;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.regex.Pattern;

public abstract class CommandBase<T extends PluginBase> {
    private static final Pattern OPTIONAL_ARGUMENT_PATTERN = Pattern.compile("\\[.+?]");
    protected final String name;
    protected final boolean executableAsConsole;
    protected final PermissionProvider permission;
    protected final String[] usageArgs;
    protected final int minArgs;
    protected final int maxArgs;

    protected CommandBase(String name, boolean executableAsConsole, PermissionProvider permission, boolean limitArgs, String... usageArgs) {
        this.name = name.toLowerCase();
        this.executableAsConsole = executableAsConsole;
        this.permission = permission;
        this.usageArgs = usageArgs;

        int minArgs = 0;
        int maxArgs = limitArgs ? 0 : -1;
        for (String arg : usageArgs) {
            if (limitArgs) {
                maxArgs++;
            }

            if (!OPTIONAL_ARGUMENT_PATTERN.matcher(arg).matches()) {
                minArgs++;
            }
        }

        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
    }

    protected CommandBase(String name, boolean executableAsConsole, PermissionProvider permission, String... usageArgs) {
        this(name, executableAsConsole, permission, true, usageArgs);
    }

    protected CommandBase(String name, PermissionProvider permission, boolean limitArgs, String... usageArgs) {
        this(name, true, permission, limitArgs, usageArgs);
    }

    protected CommandBase(String name, PermissionProvider permission, String... usageArgs) {
        this(name, true, permission, true, usageArgs);
    }

    public abstract void execute(T plugin, CommandSender sender, String label, String[] args);

    public List<String> getSuggestions(T plugin, CommandSender sender, String[] args) {
        return null;
    }

    public String getUsage(String label) {
        StringBuilder builder = new StringBuilder("/" + label + " " + name);
        for (String arg : usageArgs) {
            builder.append(" ").append(arg);
        }
        return builder.toString();
    }

    public void displayUsage(CommandSender sender, String label) {
        sender.sendMessage(getUsage(label));
    }

    public boolean isValid(String[] args) {
        return args.length >= minArgs && (maxArgs == -1 || args.length <= maxArgs);
    }

    public boolean testPermission(CommandSender sender) {
        return permission.test(sender);
    }

    public String getName() {
        return name;
    }

    public boolean isExecutableAsConsole() {
        return executableAsConsole;
    }

    public PermissionProvider getPermission() {
        return permission;
    }
}
