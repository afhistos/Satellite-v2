package be.afhistos.satellitev2.audio;

import be.afhistos.satellitev2.DefaultEmbed;
import be.afhistos.satellitev2.Satellite;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audio.hooks.ConnectionListener;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class AudioUtils extends ListenerAdapter {
    static AudioUtils instance;
    public static AudioUtils getInstance(){return instance;}
    private static final float[] BASS_BOOST = { 0.2f, 0.15f, 0.1f, 0.05f, 0.0f, -0.05f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f,-0.1f, -0.1f, -0.1f, -0.1f };

    private final AudioPlayerManager playerManager;
    public final Map<Long, GuildMusicManager> musicManagerMap;

    public AudioUtils(){
        this.musicManagerMap = new HashMap<>();
        instance = this;
        this.playerManager = new DefaultAudioPlayerManager();
        playerManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);
        playerManager.getConfiguration().setOpusEncodingQuality(AudioConfiguration.OPUS_QUALITY_MAX);
        playerManager.getConfiguration().setFilterHotSwapEnabled(true);
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(SoundCloudAudioSourceManager.builder().withAllowSearch(true).build());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public synchronized GuildMusicManager getGuildAudioPlayer(Guild g){
        long id = g.getIdLong();
        GuildMusicManager musicManager = musicManagerMap.get(id);
        if(musicManager == null){
            musicManager = new GuildMusicManager(playerManager, g.getId());
            musicManagerMap.put(id, musicManager);
        }
        g.getAudioManager().setSendingHandler(musicManager.getSendHandler());
        return musicManager;
    }

    public AudioTrack getPlayingTrack(Guild g){return getGuildAudioPlayer(g).player.getPlayingTrack();}
    public EmbedBuilder getNowPlayingEmbed(Guild g, User asker){
        AudioTrack t = getGuildAudioPlayer(g).player.getPlayingTrack();
        EmbedBuilder builder = new DefaultEmbed(asker);
        if(t == null){
            builder.setTitle("Morceau actuellement joué dans "+g.getName());
            builder.setDescription("Aucun morceau n'est joué pour le moment!");
            builder.addField("Pour jouer de la musique:", Satellite.getClient().getPrefix()+"play <Lien>", true);
            builder.addField("Pour tout savoir sur les commandes musicales:", Satellite.getClient().getPrefix()+"help", true);
        }else{
            AudioTrackInfo info = t.getInfo();
            builder.setTitle("Morceau actuellement joué dans "+g.getName(), info.uri);
            builder.addField(info.author.replace(" - Topic",""),
                    (getGuildAudioPlayer(g).player.isPaused()?":pause_button: ":":arrow_forward: ")+info.title,false);
            builder.addField("Position:", getMusicTimestamp(t.getPosition()) + "/"
                    + getMusicTimestamp(t.getDuration()),true);
            builder.addField("Status:", translateStatus(t), true);
            builder.addField("Joue un stream ?", (info.isStream ? "Oui" : "Non"), true);
            builder.addField("Mode de lecture:", getPlayingType(g), true);
            builder.addField("Volume:", getGuildAudioPlayer(g).player.getVolume()+"%",true);
            builder.addField("amplification des basses:",
                    getGuildAudioPlayer(g).isBassActive() ? "Activée" :"Désactivée", true);
            builder.setThumbnail("https://img.youtube.com/vi/"+t.getIdentifier()+"/hqdefault.jpg");
        }
        return builder;
    }
    public String getPlaylistString(Guild g){
        StringBuilder sb = new StringBuilder();
        GuildMusicManager manager = getGuildAudioPlayer(g);
        AudioTrack current = manager.player.getPlayingTrack();
        LinkedList<AudioTrack> queue = manager.scheduler.getQueue();
        if(queue.isEmpty() && current == null){
            sb.append("La playlist est vide! Remplis la avec la commande ²play :)");
        }else{
            int trackCount = 0;
            long queueLength= 0;
            sb.append("Playlist actuelle: Nombre de morceaux: **").append(queue.size() + 1).append("**\n");
            sb.append(":arrow_forward: ").append("`[").append(getMusicTimestamp(current.getPosition()))
                    .append("/").append(getMusicTimestamp(current.getDuration())).append("]`")
                    .append(current.getInfo().title).append("\n");
            for (AudioTrack track : queue){
                queueLength += track.getDuration();
                if(trackCount < 10){
                    sb.append(getNumberEmote(trackCount+1)).append(" `[")
                            .append(getMusicTimestamp(track.getDuration())).append("]`");
                    sb.append(track.getInfo().title).append("\n");
                    trackCount++;
                }
            }
            queueLength = queueLength + (current.getDuration() - current.getPosition());
            sb.append("\n").append("Durée totale de la playlist: **").append(getMusicTimestamp(queueLength)).append("**");
        }
        return sb.toString();
    }
    private static String getNumberEmote(int number) {
        String emote;
        switch (number){
            case 0:
                emote = ":zero:";
                break;
            case 1:
                emote =":one:";
                break;
            case 2:
                emote=":two:";
                break;
            case 3:
                emote=":three:";
                break;
            case 4:
                emote=":four:";
                break;
            case 5:
                emote=":five:";
                break;
            case 6:
                emote=":six:";
                break;
            case 7:
                emote=":seven:";
                break;
            case 8:
                emote = ":eight:";
                break;
            case 9:
                emote=":nine:";
                break;
            case 10:
                emote=":keycap_ten:";
                break;
            default:
                emote=":asterisk:";
        }
        return emote;

    }

    private String getPlayingType(Guild g) {
        TrackScheduler scheduler = getGuildAudioPlayer(g).scheduler;
        if(scheduler.isRepeatingOnce()){
            return "Répète le même morceau";
        }else if(scheduler.isRepeatingPlaylist()){
            return "Répète la playlist";
        }else{
            return "Joue la liste de lecture";
        }
    }

    private String translateStatus(AudioTrack t) {
        switch (t.getState()){
            case PLAYING:
                return "Joue de la musique";
            case LOADING:
                return "Charge une musique";
            case SEEKING:
                return "Recherche la musique demandée";
            case FINISHED:
                return "Ne joue plus de musique";
            default:
                return "Attends une musique à jouer";
        }
    }

    public boolean setVolume(Guild g,int value){
        GuildMusicManager manager = getGuildAudioPlayer(g);
        manager.player.setVolume(value);
        if(manager.hasEMP()){
            manager.embeddedPlayer.updateEmbedPlayer();
        }
        return manager.player.getVolume() == value;
    }

    /**
     * @param e The event, used to reply (SlashCommand)
     * @param trackUrl the trackUrl (if start with 'ytsearch:', it is a youtube search)
     * @param limit if it is a youtube search, the number of track loaded
     * @param addFirst set to true to add song on top of queue
     * @param clearPlaylist set to true to clear playlist before adding requested song(s)
     * @param pos play the track at saved pos (only if queue is empty)
     */
    public void loadAndPlay(final SlashCommandEvent e, final String trackUrl, int limit, boolean addFirst, boolean clearPlaylist, long pos){
        GuildMusicManager musicManager = getGuildAudioPlayer(e.getGuild());
        if(clearPlaylist){
            musicManager.scheduler.clearQueue();
        }
        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                track.setPosition(pos);
                if(addFirst){
                    musicManager.scheduler.queueFirst(track);
                }else{
                    musicManager.scheduler.queue(track);
                }
                e.reply("Ajout de "+track.getInfo().title+" à la file d'attente. Taille de la playlist: " +
                        (musicManager.scheduler.getQueue().size() + 1)+ " morceau(x).").queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                String artists = "";
                String artist;
                if(limit == 1 && playlist.isSearchResult()){
                    AudioTrack track = playlist.getTracks().get(0);
                    if(addFirst){
                        musicManager.scheduler.queueFirst(track);
                    }else{
                        musicManager.scheduler.queue(track);
                    }
                    e.reply("Ajout de "+track.getInfo().title+" à la file d'attente. Taille de la playlist: "+
                            (musicManager.scheduler.getQueue().size() + 1)+ " morceau(x).").queue();
                }else{
                    ArrayList<AudioTrack> tracks = new ArrayList<>();
                    for (int i = 0; i < limit && i < playlist.getTracks().size(); i++) {
                        AudioTrack currentTrack = playlist.getTracks().get(i);
                        tracks.add(currentTrack);
                        artist = currentTrack.getInfo().author;
                        artist = artist.replace(" - Topic", "");
                        if(!artists.contains(artist) && i <= 5){
                            artists = artists.concat(artist).concat(", ");
                        }
                    }
                    if(addFirst){
                        //We need to go backward or the playlist will be inverted
                        for (int j = tracks.size()- 1; j >= 0 ; j--) {
                            musicManager.scheduler.queueFirst(tracks.get(j));
                        }
                    }else{
                        for (AudioTrack track : tracks){
                            musicManager.scheduler.queue(track);
                        }
                    }
                    if(artists.endsWith(", ")){
                        artists = artists.substring(0, artists.length() - 2);
                    }
                    e.reply("Ajout de "+tracks.size()+" morceau(x) à la playlist, comprennant "+artists).queue();

                }
            }

            @Override
            public void noMatches() {
                e.reply("Aucun morceau n'a été trouvé à l'adresse __"+trackUrl+"__").queue();
            }

            @Override
            public void loadFailed(FriendlyException ex) {
                e.reply("Impossible de jouer le morceau :(\n"+ex.getMessage()).queue();
            }
        });
    }

    private void play(GuildMusicManager musicManager, AudioTrack track, boolean addFirst) {
        if(addFirst){
           LinkedList<AudioTrack> queueClone = musicManager.scheduler.getQueue();
           queueClone.offerFirst(track);
            musicManager.scheduler.setQueue(queueClone);
        }else{
            musicManager.scheduler.queue(track);
        }
    }
    public boolean skipSong(Guild g){
        GuildMusicManager musicManager = getGuildAudioPlayer(g);
        String oldTrackId = musicManager.player.getPlayingTrack().getIdentifier();
        musicManager.scheduler.skip();
        return !oldTrackId.equals(musicManager.player.getPlayingTrack().getIdentifier());
    }
    public boolean connectToFirstVoiceChannel(AudioManager audio){
        if(!audio.isConnected()){
            audio.openAudioConnection(audio.getGuild().getVoiceChannels().get(0));
            return audio.isConnected();
        }
        return false;
    }
    public void connectToVoiceChannel(AudioManager audio, AudioChannel vc, boolean goToFirst){
        if(!audio.isConnected()) {
            audio.openAudioConnection(vc);
            audio.setConnectionListener(new ConnectionListener() {
                @Override
                public void onPing(long l) {

                }

                @Override
                public void onStatusChange(@Nonnull ConnectionStatus connectionStatus) {
                    if (connectionStatus == ConnectionStatus.DISCONNECTED_AUTHENTICATION_FAILURE ||
                            connectionStatus == ConnectionStatus.ERROR_CONNECTION_TIMEOUT ||
                            connectionStatus == ConnectionStatus.DISCONNECTED_LOST_PERMISSION) {
                        if(goToFirst) connectToFirstVoiceChannel(audio);
                    }
                }

                @Override
                public void onUserSpeaking(@Nonnull User user, boolean b) {

                }
            });
        }
    }

    public void invertBass(Guild g){
        GuildMusicManager manager = getGuildAudioPlayer(g);
        if(manager.isBassActive()){// Bass must be disabled
            for (int i = 0; i < BASS_BOOST.length; i++) {
                manager.equalizer.setGain(i, 0f);

            }
            manager.player.setVolume(90);
            manager.setBassActive(false);
        }else{ //Bass must be enabled
            for (int i = 0; i < BASS_BOOST.length; i++) {
                manager.equalizer.setGain(i, BASS_BOOST[i]);

            }
            manager.player.setVolume(50);
            manager.setBassActive(true);
        }
        if(manager.hasEMP()){
            manager.embeddedPlayer.updateEmbedPlayer();
        }
    }


    public EqualizerFactory getGuildEqualizer(Guild g) {
        return getGuildAudioPlayer(g).equalizer;
    }

    public String getMusicTimestamp(long ms){
        int sec = (int) (ms / 1000) % 60;
        int min = (int) ((ms /60000)%60);
        int h = (int) ((ms/(1000 * 60 * 60)) % 24);
        if(h > 0){
            return String.format("%02d:%02d:%02d", h,min, sec);
        }else{
            return String.format("%02d:%02d", min,sec);
        }
    }

    public void stopMusic(Guild g){
        GuildMusicManager manager = getGuildAudioPlayer(g);
        manager.player.stopTrack();
        manager.scheduler.clearQueue();
        manager.player.destroy();
        musicManagerMap.remove(g.getIdLong());
        if(g.getSelfMember().getVoiceState().inAudioChannel()){
            g.getAudioManager().closeAudioConnection();
        }
    }

    public TextChannel retrieveEMP(Guild g){
        TextChannel channel = null;
        for(TextChannel c : g.getTextChannelsByName("sate-lecteur", false)){
            if(c.getTopic() != null && c.getTopic().startsWith("emp-channel-"+g.getId())){
                channel = c;
                break;
            }
        }
        return channel;
    }
}