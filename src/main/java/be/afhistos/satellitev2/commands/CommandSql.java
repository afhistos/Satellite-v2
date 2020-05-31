package be.afhistos.satellitev2.commands;

import be.afhistos.satellitev2.BotUtils;
import be.afhistos.satellitev2.Satellite;
import be.afhistos.satellitev2.database.QueryResult;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.TableBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandSql extends Command {

    public CommandSql(){
        this.name = "sql";
        this.ownerCommand=  true;
        this.category = new Category("Management");
        this.guildOnly= false;
        this.hidden = true;
        this.help = "Éxécute une requête SQL et affiche le résultat";
    }

    @Override
    protected void execute(CommandEvent e) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Requête SQL sur la base de données Satellite");
        builder.setFooter("Notez que seul les propriétaires du bot peuvent éxécuter cette commande.");
        builder.setDescription(":arrows_counterclockwise: Requête en cours de traitement...");
        e.getTextChannel().sendMessage(builder.build()).queue((message -> {
            execAndEditMessage(message, e.getArgs(), builder);
        }));
    }

    private void execAndEditMessage(Message msg, String query, EmbedBuilder builder){
        long start = System.currentTimeMillis();
        String rawResult = null;
        builder.addField("Éxécution de", "```SQL\n"+query+"\n```", false);
        msg.editMessage(builder.build()).queue();
        try{
            QueryResult qr = Satellite.getSqlInstance().getInstance().execute(query);
            ResultSet r = qr.getResult();
            ResultSetMetaData rmd = r.getMetaData();
            List<List<String>> list = new ArrayList<>();
            while(r.next()) {
                for (int i = 1; i < rmd.getColumnCount(); i++) {
                    list.add(Arrays.asList(rmd.getColumnName(i), rmd.getColumnTypeName(i), r.getObject(i).toString()));
                }
            }
            String[][] values = new String[list.size()][];
            int i = 0;
            for(List<String> nestedList : list){
                values[i++] = nestedList.toArray(new String[nestedList.size()]);
            }
            TableBuilder tb = new TableBuilder();
            tb.setAlignment(TableBuilder.Alignment.CENTER);
            tb.setBorders(TableBuilder.Borders.newPlainBorders("-", "|", "+"));
            tb.setName("Valeurs demandées dans la table "+rmd.getTableName(1));
            tb.addHeaders("Colonne", "Type", "Valeur");
            tb.codeblock(true);
            tb.setValues(values);
            qr.close();
            builder.setDescription("Commande éxécutée avec succès !");
            builder.addField("résultats:", tb.build(), false);
            String time = BotUtils.getTimestamp((System.currentTimeMillis() - start), true);
            builder.addField("Temps de traitement:", time, false);
            msg.editMessage(builder.build()).queue();
        }catch (SQLSyntaxErrorException t){
            t.printStackTrace();
            builder.setDescription("\u274c Aucune valeur retournée!");
            builder.addField("Erreur:", "```Java\nSQLSTATE: "+t.getSQLState()+"\nCause: "+ t.getClass()
                    .getName()+"\n Message additionnel: "+ (t.getMessage() == null ? "\\" : t.getMessage())+"\n```", false);
            msg.editMessage(builder.build()).queue();
        } catch (SQLException t) {
            t.printStackTrace();
            builder.setDescription("\u274c Aucune valeur retournée!");
            builder.addField("Erreur:", "```Java\nSQLSTATE: "+t.getSQLState()+"\nCause: "+ t.getClass()
                    .getName()+"\n Message additionnel: "+ (t.getMessage() == null ? "\\" : t.getMessage())+"\n```", false);
            msg.editMessage(builder.build()).queue();
        } catch (NullPointerException ign){
            builder.setDescription("\u274c Impossible d'établir la connexion avec la base de données !");
            builder.addField("Fix:", "Essayer de lancer Satellite sur la bonne machine.", false);
            msg.editMessage(builder.build()).queue();
        }
    }
}
