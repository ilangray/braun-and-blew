enum Property {
  DEPT("Department"), SPONSOR("Sponsor"), YEAR("Year");
  
  public final String name;
  
  private Property(String name) {
    this.name = name;
  }

  public String toString() {
    return name; 
  }

};
