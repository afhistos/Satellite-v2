package be.afhistos.satellitev2.commands.handler;

import net.dv8tion.jda.api.Permission;

public abstract class CommandBase {
    protected String name = "null";
    protected String help = "Aucune aide disponible";
    protected String arguments = null;
    protected String[] aliases = null;
    protected Category category = new Category();
    protected boolean guildOnly = false;
    protected String userRequiredRole = null;
    protected Permission[] userRequiredPermissions = new Permission[0];
    protected String botRequiredRole = null;
    protected Permission[] botRequiredPermissions = new Permission[0];
    protected boolean ownerCommand = false;
    protected boolean hidden = false;
    //Format: "Emoji erreur" Je n'ai pas la permission 'Permission manquante' dans 'Nom du salon'
    protected static String BOT_NO_PERM = "%s Je n'ai pas la permission '%s' dans %s!";
    //Format: "Emoji erreur" Tu dois avoir la permission 'Permission manquante' dans 'Nom du salon' pour utiliser cette commande!
    protected static String USER_NO_PERM = "%s Tu dois avoir la permission '%s' dans %s pour utiliser cette commande!";

    protected abstract void execute(CommandEvent event);

    public String getName() {
        return name;
    }

    public String getHelp() {
        return help;
    }

    public String getArguments() {
        return arguments;
    }

    public String[] getAliases() {
        return aliases;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isGuildOnly() {
        return guildOnly;
    }

    public String getUserRequiredRole() {
        return userRequiredRole;
    }

    public Permission[] getUserRequiredPermissions() {
        return userRequiredPermissions;
    }

    public String getBotRequiredRole() {
        return botRequiredRole;
    }

    public Permission[] getBotRequiredPermissions() {
        return botRequiredPermissions;
    }

    public boolean isOwnerCommand() {
        return ownerCommand;
    }

    public boolean isHidden() {
        return hidden;
    }

    public static String getBotNoPerm() {
        return BOT_NO_PERM;
    }

    public static String getUserNoPerm() {
        return USER_NO_PERM;
    }
}
