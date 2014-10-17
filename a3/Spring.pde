// This is what you think it is
class Spring extends ForceSource {
  public float restLen;
  
  public Spring(Node endA, Node endB, float k, float restLen) {
    super(endA, endB, k);
    this.restLen = restLen;
  }
}
