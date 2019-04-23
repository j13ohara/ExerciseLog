import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.JFileChooser;

/*
 * This program allows the user to keep track of their various exercises
 * You will be able to add a new exercise to log and be able to clear the list to make
 * a new one.
 * This program also allows the user to save and load their logs, so that they keep adding 
 * on to them as they continue to work out weekly.
 * The official name of the program is the "Exercise Log App"
 * This was done solo by Jan O'Hara for Dr. Klump's CPSC 245 Object Oriented Programming Class
 * for the Fall 2017 Semester.
 * This program unfortunately only serializes data in text form.
 * The view class is the ExercisePanel
 * The controller is the ExerciseFrame
 * The model is Exercise
 * I would like to add the feature to filter the log by area worked out, or name of exercise
 */
class Exercise {
	private String name;
	private String bodyPartWorked;
	private int weight;
	private String date;
	
	public void setName(String n) {
		name =n;
	}
	public String getName() {
		return name;
	}
	public void setBodyPartWorked(String b) {
		bodyPartWorked = b;
	}
	public String getBodyPartWorked() {
		return bodyPartWorked;
	}
	public void setWeight(int w) {
		weight = w;
	}
	public int getWeight() {
		return weight;
	}
	public void setDate(String d) {
		date = d;
	}
	public String getDate() {
		return date;
	}
	@Override
	public String toString() {
		return String.format("%s,%s,%s,%d", date,name,bodyPartWorked,weight);
	}
	public Exercise(String d, String n, String b, int w ){
		date = d;
		name = n;
		bodyPartWorked = b;
		weight = w;
	}
	public Exercise() {
		date = "January 1, 1970";
		name = "N/A";
		weight = 0;
		bodyPartWorked = "Nothing";
	}
}

class ExercisePanel extends JPanel{
	private ArrayList<Exercise> exercises;
	private String output = "Please enter a exercise";
	private int currentY = 10;
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
			g.setColor(Color.black);
			for(Exercise e : exercises) {
				output = String.format("Date: %s, Exercise: %s, Area Worked Out: %s, Weight(if applicable): %d", e.getDate(),e.getName(),e.getBodyPartWorked(),e.getWeight());
				g.drawString(output, 10, currentY);
				currentY = currentY + 20;
			}	
		}
		
	public void setCurrentY(int y) {
		currentY = y;
	}
	public ExercisePanel(ArrayList<Exercise> exercises) {
		this.exercises = exercises;
	}
}

class ExerciseFrame extends JFrame implements ActionListener{
	private ArrayList<Exercise> exercises;
	private String currentName;
	private String currentDate;
	private String currentArea;
	private int currentWeight;
	Exercise currentEx;
	public void actionPerformed(ActionEvent e) {
		currentName = JOptionPane.showInputDialog("Please enter the name of exercise: ");
		if(currentName == null) {currentName = "N/a";}
		currentDate = JOptionPane.showInputDialog("Please enter the date you did the exercise: ");
		if(currentDate == null) {currentDate ="N/a";}
		currentArea = JOptionPane.showInputDialog("Please enter the body area worked out: (Arms, Abs, Chest, Shoulders, or Legs");
		if(currentArea == null) {currentArea = "N/a";}
		currentWeight = Integer.parseInt((JOptionPane.showInputDialog("Please enter how much weight you used (If not applicable, enter 0)")));
		if(currentWeight == 0) {currentWeight = 0;}
		currentEx = new Exercise(currentDate,currentName,currentArea,currentWeight);
		exercises.add(currentEx);
		repaint();
	}
	public void newList(ExercisePanel epan) {
		exercises.clear();
		epan.setCurrentY(10);
		repaint();
	}
	public void setupMenu(ExercisePanel ep) {
		JMenuBar menu = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		file.add(exit);
		JMenuItem addEx = new JMenuItem("Add Exercise");
		addEx.addActionListener(this);
		file.add(addEx);
		JMenuItem clearEx = new JMenuItem("New List");
		clearEx.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				newList(ep);
			}
		});
		JMenuItem load = new JMenuItem("Load");
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				if(jfc.showOpenDialog(null)== JFileChooser.APPROVE_OPTION) {
					ExerciseIO eio = new ExerciseIO();
					if(eio.loadExs(exercises, jfc.getSelectedFile())) {
					JOptionPane.showMessageDialog(null,"Exercises loaded");
					}
				}else {
					JOptionPane.showMessageDialog(null, "Failed to load");
				}
			}
		});
		file.add(load);		
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExerciseIO eio = new ExerciseIO();
				JFileChooser jfc = new JFileChooser();
				if(jfc.showSaveDialog(null)== JFileChooser.APPROVE_OPTION) {	
					eio.saveEx(exercises, jfc.getSelectedFile());
				}
			}
		});
		file.add(save);
		file.add(clearEx);
		menu.add(file);
		setJMenuBar(menu);
	}
	public void setupUI() {
		setBounds(100,100,1000,700);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Exercise Log");
		Container c = getContentPane();
		ExercisePanel ep = new ExercisePanel(exercises);
		c.setLayout(new BorderLayout());
		JPanel botPan = new JPanel();
		JButton addNew = new JButton("Add New Exercise");
		addNew.addActionListener(this);
		botPan.add(addNew);
		JButton clearList = new JButton("New List");
		clearList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newList(ep);
			}
		});
		botPan.add(clearList);
		c.add(botPan, BorderLayout.SOUTH);
		c.add(ep, BorderLayout.CENTER);
		setupMenu(ep);
	}
	public ExerciseFrame(ArrayList<Exercise> ex) {
		exercises = ex;
		setupUI();
	}
}
class ExerciseIO{
	public boolean saveEx(ArrayList<Exercise> exs, String fname) {
		try {
			File f = new File(fname);
			return saveEx(exs,f);
		}catch(Exception ex) {
			return false;
		}
	}
	public boolean saveEx(ArrayList<Exercise> exs, File f) {
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));
			for(Exercise e : exs) {
				pw.println(e);
			}
			pw.close();
			return true;
		}catch(Exception ex) {
			return false;
		}
	}
	public boolean loadExs(ArrayList<Exercise> exs,File f) {
		try {
			Scanner sc = new Scanner(f);
			String line;
			String[] parts;
			String d,n,b;
			int w;
			Exercise e;
			while(sc.hasNextLine()) {
				line=sc.nextLine().trim();
				if(!line.equals("")) {
					parts = line.split(",");
					d = parts[0];
					n = parts[1];
					b = parts[2];
					w = Integer.parseInt(parts[3]);
					e = new Exercise(d,n,b,w);
					exs.add(e);
				}
			}
			sc.close();
			return true;
		}catch(Exception ex) {
			return false;
		}
	}
	
}
public class ExerciseLogApp {
	public static void main(String[] args) {
		ArrayList<Exercise> exercises = new ArrayList<Exercise>();
		ExerciseFrame ef = new ExerciseFrame(exercises);
		ef.setVisible(true);
	}
}
