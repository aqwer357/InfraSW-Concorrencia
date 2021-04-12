package player;

import java.util.Random;

public class Song {
    private String Name;
    private int Duration;
    private boolean Playing;

    public Song(String name){
        this.Name = name;
        this.Duration = time();
        this.Playing = false;
    }

    public int time(){
        Random random = new Random();
        int time = random.nextInt(6) + 6;
        return time;
    }

    //Getters and setters
    public int getDuration() {
        return Duration;
    }
    public void setDuration(int duration) {
        Duration = duration;
    }

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

    public void setPlaying(boolean playing) {
        Playing = playing;
    }
    public boolean isPlaying() {
        return Playing;
    }
}
