class Node {
  public Point pos;
  public Vector vel;
  public Vector acc;
  public Vector netForce;
  public boolean fixed;
  public int id;
  public float mass;
  public float radius;
  
  public Node(int id, float mass) {
    this.pos = new Point();
    this.vel = new Vector();
    this.acc = new Vector();
    public Vector netForce = new Vector();
    this.fixed = false;
    this.id = id;
    this.mass = mass;
    this.radius = sqrt(mass / PI);
  }
}
