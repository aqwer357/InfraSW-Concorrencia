package player;

import java.util.Random;

public class Song {
    private String Name;
    private int Duration;

    public Song(String name){
        this.Name = name;
        this.Duration = time();
    }

    public int time(){
        Random random = new Random();
        int time = random.nextInt(60000) + 60000;
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
}
