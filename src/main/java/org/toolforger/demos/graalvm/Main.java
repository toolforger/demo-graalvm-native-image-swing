package org.toolforger.demos.graalvm;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main extends Frame {

  public Main() {
    Button btn = new Button("I do nothing.");
    btn.setBounds(50, 50, 50, 50);
    add(btn);
    setSize(400, 150);
    setLocationRelativeTo(null);
    setTitle("Hello AWT!");
    setLayout(new FlowLayout());
    setVisible(true);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
        dispose();
      }
    });
  }

  public static void main(String[] args) {
    new Main();
  }

}
