package be.afhistos.satellitev2.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

/*
    Specific command for owners
 */
public class CommandConfinement extends SlashCommand {
    private String CONFINED_ROLE_ID = "610088023295524884";
    private String OP_ROLE_ID = "583291626261184543";

    public CommandConfinement(){
        this.name ="confinement";
        this.aliases = new String[]{"cf", "confine", "confiné"};
        this.category = new Category("Utilitaires");
        this.hidden = true;
        this.ownerCommand = true;
        this.guildOnly = true;
    }

    @Override
    protected void execute(SlashCommandEvent e) {
        if(!e.getGuild().getId().equals("484372589951582218")){
            e.deferReply();
            return;
        }
        //First, remove all roles from member
        e.getMember().getRoles().forEach(role -> {
            e.getGuild().removeRoleFromMember(e.getMember(), role).queue();
        });
        //Check if the member is already confined
        if(e.getMember().getRoles().contains(e.getGuild().getRoleById(CONFINED_ROLE_ID))){//Already confined
            e.getGuild().addRoleToMember(e.getMember(), e.getGuild().getRoleById(OP_ROLE_ID)).queue();
            e.reply("Si ce message te mentionne, tu es bien OP! "+e.getGuild().getRoleById(OP_ROLE_ID).getAsMention()).queue();
        }else{//Not confined yet
            e.getGuild().addRoleToMember(e.getMember(), e.getGuild().getRoleById(CONFINED_ROLE_ID)).queue();
                e.reply("Si ce message te mentionne, tu es bien confiné! "+
                        e.getGuild().getRoleById(CONFINED_ROLE_ID).getAsMention()+
                        "\n(Et regarde à gauche abruti)").queue();
        }

    }
}
