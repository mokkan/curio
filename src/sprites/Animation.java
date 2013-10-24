package sprites;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;

public class Animation {
    
    private Texture sheet;
    private ArrayList<Frame> frames;
    private int width;
    private int height;
    
    public Animation(Texture sheet, int width, int height) {
        this.sheet = sheet;
        this.width = width;
        this.height = height;
        frames = new ArrayList<Frame>();
    }
    
    public void addFrame(int x, int y, float time) {
        frames.add(new Frame(sheet, x, y, width, height, time));
    }
    
    public ArrayList<Frame> getFrames() {
        return frames;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
}
