package player;

import java.util.Random;

public class Song {
    private String Name;
    private int Duration;
    private boolean Playing;
    private boolean done;

    public Song(String name){
        this.Name = name;
        this.Duration = time();
        this.Playing = false;
        this.done = false;
    }

    public int time(){
        Random random = new Random();
        int time = random.nextInt(4) + 4; // Duracao da musica em segundos
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

    public void setDone(boolean done) {
        this.done = done;
    }
    public boolean isDone() {
        return done;
    }
}
