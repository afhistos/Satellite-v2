package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.Satellite;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;

/*  Structure
 *  Catégorie: Satellite-Utils
 *  Salons:
 *   -- logs
 *   -- audio-player
 */


public class CommandSetup extends Command {

    public CommandSetup(){
        this.name = "setup";
        this.arguments = "[all]/[[audio]/[logs]]";
        this.help = "Cette commande permet de mettre en place les différents système de Satellite.\n**Arguments:**\n"+
                "__all__: Mets en place tous les systèmes (argument par défaut)\n"+
                "__audio__: Mets en place le système de lecteur graphique\n"+
                "__logs__: Mets en place le système de logs\n"+
                "Tous les arguments peuvent être combinés (exemple: `²setup audio logs`).\nSeuls les membres ayant la "+
                "permission `Administrateur` auront accès à ces salons (modifiable)";
        this.guildOnly = true;
        this.category = new Category("Divers");
        this.botPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
    }

    @Override
    protected void execute(CommandEvent e) {
        if(!e.getMember().isOwner()){
            e.replyWarning("Seul le propriétaire de ce serveur ("+ e.getGuild().getOwner().getUser().getAsMention()+
                    ") peut éxécuter cette commande.");
            return;
        }
        String mode = e.getArgs().toUpperCase();
        if(mode.isEmpty() || !(mode.equalsIgnoreCase("ALL") || mode.equalsIgnoreCase("AUDIO")||
                mode.equalsIgnoreCase("LOGS"))){
            e.replyError("`"+mode+"` n'est pas un argument valide! Veuillez choisir parmis `all | audio | logs`\n"+
                    "Pour plus d'informations, faites "+e.getClient().getPrefix()+"help "+this.name);
            return;
        }
        final boolean setLogs = (mode.contains("ALL") || mode.contains("LOGS")),
                setAudio = (mode.contains("ALL") || mode.contains("AUDIO"));

        e.reply("Lancement de la configuration du serveur... (mode: "+mode+")");
        long everyone_id = e.getGuild().getPublicRole().getIdLong();
        e.getGuild().createCategory(BotUtils.DEFAULT_CAT_NAME).queue(cat ->{
            if(setLogs){
                cat.createTextChannel(BotUtils.LOGS_CHANNEL_NAME).addRolePermissionOverride(everyone_id,
                        null,BotUtils.CAT_PERMISSIONS_DENY)
                        .setSlowmode(0)
                        .setTopic("C'est ici que vous serez mis au courant des actions de Satellite sur le serveur")
                        .queue(channel -> {
                    boolean b = Satellite.getSqlInstance().executeData("UPDATE guilds_data SET logs_channel_id="+
                            channel.getId()+" WHERE guild_id="+e.getGuild().getId());
                    if(b){
                        e.reply("Salon de logs créé!");
                    }else{
                        e.reply("Une erreur est survenue lors de la création du salon de Logs");
                    }
                });
            }
            if(setAudio){
                cat.createTextChannel(BotUtils.LMI_CHANNEL_NAME).addRolePermissionOverride(everyone_id,
                        null,BotUtils.CAT_PERMISSIONS_DENY)
                        .setSlowmode(0)
                        .setTopic("C'est içi que le LMI prends place!")
                        .queue(channel -> {
                    boolean b = Satellite.getSqlInstance().executeData("UPDATE guilds_data SET lmi_channel_id=" +channel.getId()+
                            " WHERE guild_id="+e.getGuild().getId());
                    if(b){
                        e.reply("Salon du Lecteur Multimédia Intégré créé!");

                    }else{
                        e.reply("Une erreur est survenue lors de la création du salon audio");
                    }
                });
            }
        });


    }
}
