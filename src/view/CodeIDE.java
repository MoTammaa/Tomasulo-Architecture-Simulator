package view;

import javax.swing.*;
import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class CodeIDE extends JFrame {
    public CodeIDE() {
        setTitle("Simple IDE");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 18));

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        JButton compileButton = new JButton("Compile");
        compileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textArea.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter some code to compile :).");
                    return;
                }
                writeToOutputFile(textArea.getText());
                TomasuloInputs.processTomasuloInputs();

                System.out.println("Compiling... and scheduling");
                dispose();
            }
        });

        add(compileButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    private static void CreateNwriteFile(String input) {
        String [] date = new Date().toString().split(" ");
        String [] time = date[3].split(":");
        long millis=System.currentTimeMillis();

        // creating a new object of the class Date
        String [] date2 = new java.sql.Date(millis).toString().split("-");
        int hour = Integer.parseInt(time[0]);
        String filename ="output-" +date2[0]+date2[1]+date2[2]+"-"+ ((hour>12)?"0"+(hour-12):hour) + time[1]+ time[2];

        try {
            File myObj = new File(filename + ".txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred writing file.");
        }

        WriteToFile(filename, input);
    }
    private static void writeToOutputFile(String input) {
        WriteToFile(null, input);
    }
    private static void WriteToFile(String filename, String input) {
        filename = (filename == null)?"Program":filename;
        try {
            FileWriter myWriter = new FileWriter(filename + ".txt");
            myWriter.write(input);
            myWriter.close();
            System.out.println("Successfully wrote to the file."+'\n');
        } catch (IOException e) {
            System.err.println("An error occurred writing to file.");

        }
    }
    public static void startCoding() {
        new CodeIDE();
    }

    public static void main(String[] args) {
        new CodeIDE();
    }
}