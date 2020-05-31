package be.afhistos.satellitev2;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.Clock;
import java.time.Instant;

public class DefaultEmbed extends EmbedBuilder {

    public DefaultEmbed(User u){
        this.setColor(BotUtils.getDefaultColor());
        this.setTimestamp(Instant.now(Clock.systemDefaultZone()));
        this.setAuthor("Commande éxécutée par "+u.getAsTag(), u.getAvatarUrl(),u.getAvatarUrl());
        this.setFooter("Satellite, toujours la pour vous servir", Satellite.getBot().getSelfUser().getAvatarUrl());
    }
}
