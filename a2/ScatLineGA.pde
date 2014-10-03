
class ScatLineGA extends GraphAnimator {
 
  private final Line lg;
  
  public ScatLineGA(Line lg, float duration, float percentStart, float percentEnd) {
    super(lg, duration, percentStart, percentEnd); 
    this.lg = lg;
  }
  
  void render() {
    super.updateCurrentPercent();
    
    lg.linePercent = getCurrentPercent();
    lg.render(); 
    
    super.checkIfCompleted();
  }
  
}

