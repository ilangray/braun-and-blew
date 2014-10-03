
class ScatLineGA extends GraphAnimator {
 
  private final Line lg;
  
  public ScatLineGA(Line lg, float duration, float percentStart, float percentEnd) {
    super(lg, duration, percentStart, percentEnd); 
    this.lg = lg;
  }
  
  void render() {
//    super.render();
    super.updateCurrentPercent();
    
    println("current percent = " + getCurrentPercent());
    
    lg.linePercent = getCurrentPercent();
    println("linePercent = " + lg.linePercent);
    lg.render(); 
    
    super.checkIfCompleted();
  }
  
}

