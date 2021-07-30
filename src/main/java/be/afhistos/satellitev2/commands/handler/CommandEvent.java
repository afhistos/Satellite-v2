package be.afhistos.satellitev2.commands.handler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.utils.AttachmentOption;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class CommandEvent {
    private final MessageReceivedEvent event;
    private String args;
    private final CommandHandler handler;
    private final int MAX_MESSAGES = 3;

    public CommandEvent(MessageReceivedEvent event, String args, CommandHandler handler) {
        this.event = event;
        this.args = args == null ? "": args;
        this.handler = handler;
    }

    public MessageReceivedEvent getEvent() {
        return event;
    }

    public String getArgs() {
        return args;
    }

    public CommandHandler getHandler() {
        return handler;
    }
    //Cast le deuxieme argument pour éviter un conflit avec reply(String, File, String)
    public void reply(String message){
        reply(message, (Consumer<Message>) null, null);
    }

    public void reply(String message, Consumer<Message> success){
        reply(message, success, null);
    }

    public void reply(String message, Consumer<Message> success, Consumer<Throwable> failure){
        sendMessage(event.getChannel(), message, success, failure);
    }

    public void reply(MessageEmbed embed){
        reply(embed, null, null);
    }

    public void reply(MessageEmbed embed, Consumer<Message> success){
        reply(embed,success, null);
    }

    public void reply(MessageEmbed embed, Consumer<Message> success, Consumer<Throwable> failure){
        event.getChannel().sendMessageEmbeds(embed).queue(success, failure);
    }

    public void reply(Message message){
        reply(message, null, null);
    }

    public void reply(Message message, Consumer<Message> success){
        reply(message, success, null);
    }

    public void reply(Message message, Consumer<Message> success, Consumer<Throwable> failure){
        event.getChannel().sendMessage(message).queue(success, failure);
    }

    public void reply(File file){
        reply("", file, file.getName());
    }

    public void reply(File file, String filename){
        reply("", file, filename);
    }

    public void reply(String message,File file, String filename){
        event.getChannel().sendFile(file, filename).content(message).queue();
    }

    public void replyFormatted(String format, Object... args){
        sendMessage(event.getChannel(), String.format(format, args));
    }

    public void replyOrAlternate(MessageEmbed embed, String alternateMessage){
        try{
            event.getChannel().sendMessageEmbeds(embed).queue();
        }catch (PermissionException ign){
            reply(alternateMessage);
        }
    }

    public void replyOrAlternate(File file,String filename, String alternateMessage){
        try{
            event.getChannel().sendFile(file, filename).queue();
        }catch (Exception ign){
            reply(alternateMessage);
        }
    }

    //Cast le deuxieme argument pour éviter un conflit avec replyInDm(String, File, String)
    public void replyInDm(String message){
        replyInDm(message, (Consumer<Message>) null, null);
    }

    public void replyInDm(String message, Consumer<Message> success){
        replyInDm(message, success, null);
    }

    public void replyInDm(String message, Consumer<Message> success, Consumer<Throwable> failure){
        if(event.isFromType(ChannelType.PRIVATE)){
            reply(message, success, failure);
        }else{
            event.getAuthor().openPrivateChannel().queue(privateChannel -> {
                sendMessage(privateChannel, message);
            });
        }
    }

    public void replyInDm(MessageEmbed embed, Consumer<Message> success, Consumer<Throwable> failure){
        if(event.isFromType(ChannelType.PRIVATE)){
            reply(embed, success, failure);
        }else{
            event.getAuthor().openPrivateChannel().queue(privateChannel -> {privateChannel.sendMessageEmbeds(embed).queue(success, failure);});
        }
    }

    public void replyInDm(Message message, Consumer<Message> success, Consumer<Throwable> failure){
        if(event.isFromType(ChannelType.PRIVATE)){
            reply(message, success, failure);
        }else{
            event.getAuthor().openPrivateChannel().queue(privateChannel -> {privateChannel.sendMessage(message).queue(success, failure);});
        }
    }

    public void replyInDm(String message, File file, String filename){
        if(event.isFromType(ChannelType.PRIVATE)){
            reply(message, file, filename);
        }else{
            event.getAuthor().openPrivateChannel().queue(privateChannel -> {privateChannel.sendFile(file, filename).content(message).queue();});
        }
    }

    public void replySuccess(String message){
        reply(handler.getSuccess()+" "+message);
    }

    public void replySuccess(String message, Consumer<Message> queue){
        reply(handler.getSuccess()+" "+message, queue);
    }

    public void replyWarning(String message){
        reply(handler.getWarning()+" "+message);
    }

    public void replyWarning(String message, Consumer<Message> queue){
        reply(handler.getWarning()+" "+message, queue);
    }

    public void replyError(String message){
        reply(handler.getError()+" "+message);
    }

    public void replyError(String message, Consumer<Message> queue){
        reply(handler.getError()+" "+message, queue);
    }
    public void reactSuccess(){
        react(handler.getSuccess());
    }

    public void reactWarning(){
        react(handler.getWarning());
    }

    public void reactError(){
        react(handler.getError());
    }

    public void react(String reaction){
        if(reaction == null || reaction.isEmpty()){
            return;
        }
        try{
            event.getMessage().addReaction(reaction.replaceAll("<a?:(.+):(\\d+)>", "$1:$2")).queue();
        }catch (PermissionException ign){}
    }

    private void sendMessage(MessageChannel channel, String message){
        ArrayList<String> messages = splitMessage(message);
        for (int i = 0; i < MAX_MESSAGES; i++) {
            channel.sendMessage(messages.get(i)).queue();
        }
    }

    private void sendMessage(MessageChannel channel, String message, Consumer<Message> success) {
        ArrayList<String> messages = splitMessage(message);
        for(int i=0; i<MAX_MESSAGES && i<messages.size(); i++) {
            if(i+1==MAX_MESSAGES || i+1==messages.size()) {
                channel.sendMessage(messages.get(i)).queue(success);
            } else{
                channel.sendMessage(messages.get(i)).queue(success);
            }
        }
    }

    private void sendMessage(MessageChannel channel, String message, Consumer<Message> success, Consumer<Throwable> failure) {
        ArrayList<String> messages = splitMessage(message);
        for(int i = 0; i < MAX_MESSAGES && i < messages.size(); i++) {
            if(i + 1 == MAX_MESSAGES || i + 1 == messages.size()){
                channel.sendMessage(messages.get(i)).queue(success, failure);
            } else{
                channel.sendMessage(messages.get(i)).queue();
            }
        }
    }

    public static ArrayList<String> splitMessage(String stringtoSend)
    {
        ArrayList<String> msgs =  new ArrayList<>();
        if(stringtoSend!=null)
        {
            stringtoSend = stringtoSend.replace("@everyone", "@\u0435veryone").replace("@here", "@h\u0435re").trim();
            while(stringtoSend.length()>2000)
            {
                int leeway = 2000 - (stringtoSend.length()%2000);
                int index = stringtoSend.lastIndexOf("\n", 2000);
                if(index<leeway)
                    index = stringtoSend.lastIndexOf(" ", 2000);
                if(index<leeway)
                    index=2000;
                String temp = stringtoSend.substring(0,index).trim();
                if(!temp.equals(""))
                    msgs.add(temp);
                stringtoSend = stringtoSend.substring(index).trim();
            }
            if(!stringtoSend.equals(""))
                msgs.add(stringtoSend);
        }
        return msgs;
    }

    public SelfUser getSelfUser(){
        return event.getJDA().getSelfUser();
    }

    public Member getSelfMember(){
        return event.getGuild() == null ? null: event.getGuild().getSelfMember();
    }

    public boolean isOwner(){
        if(event.getAuthor().getId().equals(this.getHandler().getOwnerId())){
            return true;
        }
        if(this.getHandler().getCoOwnerIds() == null || this.getHandler().getCoOwnerIds().isEmpty()){
            return false;
        }
        for(String id : this.getHandler().getCoOwnerIds()){
            if(id.equals(event.getAuthor().getId())){
                return true;
            }
        }
        return false;
    }

    public User getAuthor(){
        return event.getAuthor();
    }

    public MessageChannel getChannel(){
        return event.getChannel();
    }

    public ChannelType getChannelType(){
        return event.getChannelType();
    }

    public Guild getGuild(){
        return event.getGuild();
    }

    public JDA getJDA(){
        return event.getJDA();
    }

    public Member getMember(){
        return event.getMember();
    }

    public PrivateChannel getPrivateChannel() {
        return event.getPrivateChannel();
    }

    public long getResponseNumber() {
        return event.getResponseNumber();
    }

    public TextChannel getTextChannel() {
        return event.getTextChannel();
    }
    public Message getMessage(){
        return event.getMessage();
    }

    public boolean isFromType(ChannelType channelType) {
        return event.isFromType(channelType);
    }


}
