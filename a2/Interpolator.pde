

/**
 * An Interpolator provides the ability to track the percentage
 * progress of the passage of a length of time.
 */
class Interpolator {
  
  public final float seconds;
  public final float start;
  public final float end;
  
  private float rate;
  private int startFrame;
  private int totalFrames;
 
  public Interpolator(float seconds, float start, float end) {
    this.seconds = seconds;
    this.start = start;
    this.end = end;
  } 
  
  public Interpolator start() {
    rate = frameRate;
    startFrame = frameCount;
    
    totalFrames = Math.round(rate * seconds);
    
    return this;
  }
  
  private int getElapsedFrames() {
    return frameCount - startFrame;
  }
  
  private float getPercentTime() {
    return getElapsedFrames() / (float)totalFrames; 
  }
  
  public float getInterpolatedValue() {
    float percentTime = getPercentTime();
    
//    println("percent time = " + percentTime);
    
    return lerp(start, end, getPercentTime());
  }
}
