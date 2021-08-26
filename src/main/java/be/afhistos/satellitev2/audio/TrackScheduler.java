package be.afhistos.satellitev2.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.*;

public class TrackScheduler extends AudioEventAdapter {
    private boolean repeatingOnce = false;
    private boolean repeatingPlaylist = false;
    private final AudioPlayer player;
    private final Deque<AudioTrack> queue;
    AudioTrack lastTrack;

    public TrackScheduler(AudioPlayer player){
        this.player = player;
        this.queue = new LinkedList<>();
    }

    public void queue(AudioTrack track){
        if(!player.startTrack(track, true)){
            queue.offer(track);
        }
    }

    public void queueFirst(AudioTrack track){
        queue.offerFirst(track);
    }

    public LinkedList<AudioTrack> getQueue() {
        LinkedList<AudioTrack> linkedList = new LinkedList<>();
        for (AudioTrack track : queue){
            linkedList.offer(track);
        }
        return linkedList;
    }

    public void setQueue(LinkedList<AudioTrack> list){
        clearQueue();
        for(AudioTrack track : list){
            queue.offer(track);
        }
    }
    public void shuffle(){
        Collections.shuffle((List<?>) queue);
    }
    public void skip(){player.startTrack(queue.poll(), false);}

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        this.lastTrack = track;
        if(endReason.mayStartNext){
            if(repeatingOnce) {//Repeating one song only
                player.startTrack(lastTrack.makeClone(), false);
                System.out.println("Repeating once: "+lastTrack.getInfo().title);
            }else if(repeatingPlaylist){
                queue.offer(lastTrack.makeClone());
                System.out.println("Repeating all: "+lastTrack.getInfo().title);
                skip();

            }else{
                skip();
            }
        }
    }

    public boolean isRepeatingOnce() {
        return repeatingOnce;
    }

    public void setRepeatingOnce(boolean repeatingOnce) {
        this.repeatingOnce = repeatingOnce;
        this.repeatingPlaylist = false;
    }
    public boolean invertRepeatingOnce(){ //renvoie true si la repetition est finalement activée
        if(isRepeatingOnce()){
            setRepeatingOnce(false);
        }else{
            setRepeatingOnce(true);
        }
        return this.repeatingOnce;
    }

    public boolean isRepeatingPlaylist() {
        return repeatingPlaylist;
    }

    public void setRepeatingPlaylist(boolean repeatingPlaylist) {
        this.repeatingOnce = false;
        this.repeatingPlaylist = repeatingPlaylist;
    }
    public boolean invertRepeatingPlaylist(){
        if(isRepeatingPlaylist()){
            setRepeatingPlaylist(false);
        }else{
            setRepeatingPlaylist(true);
        }
        return this.repeatingPlaylist;
    }
    public void clearQueue(){
        queue.clear();
    }

    public long getQueueDuration(){
        long duration = 0L;
        for (AudioTrack track : queue){
            duration += track.getDuration();
        }
        return duration;
    }
    public void playLastTrack(){
        player.startTrack(lastTrack.makeClone(), false);
    }
}
