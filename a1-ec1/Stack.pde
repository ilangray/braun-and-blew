
// A stack!
// owned by ilan
class Stack<T> {
  
  private final ArrayList<T> elements;
  
  public Stack() {
    elements = new ArrayList<T>();
  }
  
  // adds a new element
  public void push(T element) {
    elements.add(element);
  }
  
  // returns the top element of the stack and removes it. returns null if empty
  public T pop() {
    if (isEmpty()) {
      return null;
    }
    
    T top = top();
    elements.remove(lastIndex());
    return top;
  }
  
  // returns the top element of the stack without removing it. returns null if empty
  public T top() {
    return isEmpty()? null : elements.get(lastIndex());
  }
 
  // returns the index of the last elements in the underlying arraylist
  private int lastIndex() {
    return elements.size() - 1;
  }
  
  public boolean isEmpty() {
    return elements.isEmpty();  
  }
}
