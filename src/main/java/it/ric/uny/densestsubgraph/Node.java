package it.ric.uny.densestsubgraph;

import java.util.HashSet;

public class Node {

  private int value;
  private HashSet<Node> adjacent;

  private boolean selfLoop;

  public Node(int value) {
    this.value = value;
    this.adjacent = new HashSet<>();
  }

  public void addAdjacent(Node node){
    this.adjacent.add(node);
  }

  public int getDegree(){
    return adjacent.size();
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public boolean isSelfLoop() {
    return selfLoop;
  }

  public void setSelfLoop(boolean selfLoop) {
    this.selfLoop = selfLoop;
  }
}
