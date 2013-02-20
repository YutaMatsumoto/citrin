import javax.swing.*;
import javax.swing.text.*;

public class Console extends JTextField {
  Document doc; 

  public Console() { 
    super(); 
  }

  public Console(Document doc) { 
    this.doc = doc;
  }

}
