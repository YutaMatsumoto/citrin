import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.io.FileWriter.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.text.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.undo.AbstractUndoableEdit;

public class guiPanel extends JPanel { // implements ActionListener {
  
  // C++ source file this application has the focus on currently
  // When tab focus changes this variable should refer to the file the tab contains
  String currentCppSourceFile;

	private static final long serialVersionUID = 1L;
	//JMenuItems for File
	// private JMenuItem newFile;
	// private JMenuItem openFile;
	// private JMenuItem saveFile;
	// private JMenuItem saveAsFile;
	// private JMenuItem run;
	// private JMenuItem exit;

	//Menu items for Edit
	private JMenuItem undo;
	private JMenuItem redo;
	
	//TODO: need functionality to keep track of what action was just done
	
	//Menu Items for Help
	private JMenuItem tutorial;

	
	//constructor
	public guiPanel(){
		JMenuBar topBar = new JMenuBar(); //menu bar to hold the menus
		
		JMenu fileMenu = new JMenu("File");
		JMenu editMenu = new JMenu("Edit");
		//JMenu styleMenu = new JMenu("Style");
		JMenu helpMenu = new JMenu("Help");
		
		//menu items for the menu "File"
		// newFile = new JMenuItem("New");
		// openFile = new JMenuItem("Open");
		// saveFile = new JMenuItem("Save");
		// saveAsFile = new JMenuItem("Save As");
    // run = new JMenuItem("Run");
		// exit = new JMenuItem("Exit");

		//menu items for "Edit"
		redo = new JMenuItem("Redo");
		undo = new JMenuItem("Undo");
		
		//help menu
		tutorial = new JMenuItem("Tutorial");
		
		//add the menus to the menu bar
		topBar.add(fileMenu);
		topBar.add(editMenu);
		//topBar.add(styleMenu);
		topBar.add(helpMenu);
		
		//add the menu items to File
		// fileMenu.add(newFile);
		// fileMenu.addSeparator(); //separator between menu items
		// fileMenu.add(openFile);
		// fileMenu.addSeparator();
		// fileMenu.add(saveFile);
		// fileMenu.addSeparator();
		// fileMenu.add(saveAsFile);
		// fileMenu.addSeparator();
    // fileMenu.add(run);
		// fileMenu.addSeparator();
		// fileMenu.add(exit);
		
		
		helpMenu.add(tutorial);
		
		JPanel  myPanel = new JPanel();


	    
		JTextArea area = new JTextArea();
		JTextArea area2 = new JTextArea();
		JTextArea area3 = new JTextArea();
		//set up the area to be put into the scrool pane
		area.setColumns(30);
		area.setRows(10);
		area.setEditable(true);
		
		//add edit buttons to menu
			// TODO: get undo/redo working, need undomanager and listner; look at swing.undo
		editMenu.add(undo);
		editMenu.add(redo);
		editMenu.add(area.getActionMap().get(DefaultEditorKit.cutAction));
		editMenu.add(area.getActionMap().get(DefaultEditorKit.copyAction));
		editMenu.add(area.getActionMap().get(DefaultEditorKit.pasteAction));
		editMenu.add(area.getActionMap().get(DefaultEditorKit.selectAllAction));
		
		// Make buttons look nicer
		Action a;
	    a = area.getActionMap().get(DefaultEditorKit.cutAction);
	    a.putValue(Action.SMALL_ICON, new ImageIcon("cut.gif"));
	    a.putValue(Action.NAME, "Cut");

	    a = area.getActionMap().get(DefaultEditorKit.copyAction);
	    a.putValue(Action.SMALL_ICON, new ImageIcon("copy.gif"));
	    a.putValue(Action.NAME, "Copy");

	    a = area.getActionMap().get(DefaultEditorKit.pasteAction);
	    a.putValue(Action.SMALL_ICON, new ImageIcon("paste.gif"));
	    a.putValue(Action.NAME, "Paste");

	    a = area.getActionMap().get(DefaultEditorKit.selectAllAction);
	    a.putValue(Action.NAME, "Select All");
		
		
	    // To Do: Get style menu working
	    /*Action action = new StyledEditorKit.BoldAction();
        action.putValue(Action.NAME, "Bold");
        styleMenu.add(action); 
	    
        action = new StyledEditorKit.ItalicAction();
        action.putValue(Action.NAME, "Italic");
        styleMenu.add(action);
 
        action = new StyledEditorKit.UnderlineAction();
        action.putValue(Action.NAME, "Underline");
        styleMenu.add(action);*/
	    
		JTabbedPane tabbedPane = new JTabbedPane();
		JTabbedPane tabbedPane2 = new JTabbedPane();
		JTabbedPane tabbedPane3 = new JTabbedPane();
		
		area2.setColumns(30);
		area2.setRows(10);
		area2.setEditable(true);

		area3.setColumns(30);
		area3.setRows(10);
		area3.setEditable(true);
		
		JScrollPane scrollPane = new JScrollPane(area);
		
		JScrollPane scrollPane2 = new JScrollPane(area2);

		JScrollPane scrollPane3 = new JScrollPane(area3);
		

		
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.green));
		scrollPane2.setBorder(BorderFactory.createLineBorder(Color.red));
		scrollPane3.setBorder(BorderFactory.createLineBorder(Color.blue));
		
		tabbedPane.addTab("Program", null);
		tabbedPane.add(scrollPane);
		
		tabbedPane2.addTab("Output", null);
		tabbedPane.add(scrollPane2);
		
		tabbedPane3.addTab("States", null);
		tabbedPane.add(scrollPane3);

    Console console = new Console();
    console.setEditable(false);
    Editor editor = new Editor();


    fileMenu.add(new OpenAction(editor));
    fileMenu.add(new SaveAction("Save", editor, true));
    fileMenu.add(new SaveAction("SaveAs", editor, false));
    fileMenu.add(new RunAllAction(console));
    fileMenu.add(new ExitAction());
		
		JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabbedPane3, console);
		
		splitPane2.setOneTouchExpandable(true);
		splitPane2.setDividerLocation(200);
		
		// JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, splitPane2);
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editor, splitPane2);

		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(450);
		
		//Provide minimum sizes for the two components in the split pane
		Dimension minimumSize = new Dimension(100, 50);
		scrollPane.setMinimumSize(minimumSize);
		scrollPane2.setMinimumSize(minimumSize);
		scrollPane3.setMinimumSize(minimumSize);


		myPanel.setLayout(new BorderLayout());
		myPanel.setPreferredSize(new Dimension(400, 400));
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(800, 600));
		
		myPanel.add(splitPane);
		myPanel.setLocation(0, 0);
		add("Center", myPanel);
		add("North", topBar);
		setLocation(0,0);
		
		// newFile.addActionListener(this);
		// saveAsFile.addActionListener(this);
		// exit.addActionListener(this);

	}
	

	
	private static void createAndShowGUI(){
		// Create the container	
		JFrame frame = new JFrame("CITRIN", null);

		// Quit the application when this frame is closed:
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Create and add the content
		guiPanel panel = new guiPanel();
		frame.setLayout(new BorderLayout());
		frame.add(panel);
		
		// Display the window
		frame.pack(); // adapt the frame size to its content
		frame.setLocation(300, 20); // in pixels
		frame.setVisible(true); // Now the frame will appear on screen
		// Quit the application when this frame is closed:
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//add a window listener
		frame.addWindowListener(new WindowAdapter(){
			//save the user preferences as the window closes
			public void windowClosing(WindowEvent e){
				
				System.exit(0);
			}
		});
				
	}
		
	public static void main(String args[]){

		// Set the look and feel to that of the system
		try
		{ 
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() ); }
			catch ( Exception e )
        { System.err.println( e ); }
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			createAndShowGUI();
			
			}
			});
		
		
	}
	
  /*
	public void  saveFileAs() throws IOException  {
		  String saveFileName = JOptionPane.showInputDialog("Save File As");
		  System.out.print(saveFileName);
	}
  */

  /*
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == exit){
			System.exit(0);
		}
	}
  */

// ----------------------------------------------------------------------------
// Callback Classes 
//
// TODO : 
//
//    - NewFile Action
//    - Error checking such as making sure the file exists before saving
//
// ToConsider : 
//
//     SaveAction and OpenAction should  resides in a different class since
//     those actions sould be more related to some editing session type class
//     rather than this main application class
//
//     All these functions are using currentCppSourceFile. Hard to keep track
//     of if multiple editing session should be implemented.

  // An action to simply exit the app
  class ExitAction extends AbstractAction {

    public ExitAction() {
      super("Exit");
    }

    public void actionPerformed(ActionEvent ev) {
			System.exit(0);
    }

  }

  // An action that saves the document to a file : Supports Save and SaveAs actoins 
  class SaveAction extends AbstractAction {

    // PossibleBugSource : Saving to class member variable currentCppSourceFile
   
    JTextComponent textComponent;
    boolean saveToCurrentFile;

    // label              ... lable to show on the view
    // textComponent      ... the view and model that keeps and show the text data
    // saveToCurrentFile  ... false => prompts the user for the file, true => save to curent file
    public SaveAction(String label, JTextComponent textComponent, boolean saveToCurrentFile) {
      super(label);
      this.textComponent = textComponent;
      this.saveToCurrentFile = saveToCurrentFile;
    }

    public void actionPerformed(ActionEvent ev) {
      File file;
      if ( ! saveToCurrentFile ) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION)
          return;
        file = chooser.getSelectedFile();
        if (file == null)
          return;
      } 
      else {
        file = new File(currentCppSourceFile);
      }

      FileWriter writer = null;
      try {
        writer = new FileWriter(file);
        textComponent.write(writer);
      } catch (IOException ex) {
        JOptionPane.showMessageDialog(null,
            "File Not Saved", "ERROR", JOptionPane.ERROR_MESSAGE);
      } finally {
        if (writer != null) {
          try {
            writer.close();
          } catch (IOException x) {
          }
        }
      }
    }

  }

  // An action that opens an existing file
  class OpenAction extends AbstractAction {
    JTextComponent textComponent;

    // textComponent ... this action opens a file into this textComponent
    public OpenAction(JTextComponent textComponent) {
      super("Open");
      this.textComponent = textComponent;
    }

    // Query user for a filename and attempt to open and read the file into
    // the text component.
    public void actionPerformed(ActionEvent ev) {
      JFileChooser chooser = new JFileChooser();
      if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
        return;
      File file = chooser.getSelectedFile();
      if (file == null)
        return;

      FileReader reader = null;
      try {
        reader = new FileReader(file);
        textComponent.read(reader, null);
        currentCppSourceFile = file.getPath();
      } catch (IOException ex) {
        JOptionPane.showMessageDialog(null,
            "File Not Found", "ERROR", JOptionPane.ERROR_MESSAGE);
      } finally {
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException x) {
          }
        }
      }
    }
  }

  // An action that runs the interpreter on all the contents of the current cpp source file
  class RunAllAction extends AbstractAction {
    JTextComponent display;

    public RunAllAction(JTextComponent display) {
      super("RunAll");
      this.display = display;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      // run interpreter on currentCppSourceFile
      Interpreter interpreter = null;
      interpreter = new Interpreter();
      String interpretation = "";
      try { 
        interpretation = interpreter.runAll(currentCppSourceFile);
      } catch (IOException ex) {
        ex.printStackTrace();
      }

      // update display with the result of the interpretation
      Document doc = null;
      doc = new PlainDocument();
      try { 
        doc.insertString(0, interpretation, null);
      } catch ( BadLocationException badloc ) {
        System.out.println( "Bad offset requested : " + badloc.offsetRequested() );
      }
      display.setDocument(doc);
    }
    
  }
	
}
