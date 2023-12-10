package view;
import engine.Tomasulo;

import javax.swing.*;
import java.awt.*;

public class TomasuloInputs extends JFrame {

    public TomasuloInputs() {
        setTitle("Tomasulo Inputs");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Get the screen size
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        int screenWidth = gd.getDisplayMode().getWidth();
        int screenHeight = gd.getDisplayMode().getHeight();

        // Set the window size to the monitor's height and width
        setSize(screenWidth, screenHeight);
        setLocationRelativeTo(null);


        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);




        String[] labelsGroup1 = {"ADD Cycles: ", "SUB Cycles: ", "MULT / MULI Cycles: ", "DIV / DIVI Cycles: ", "LOAD Cycles: ", "STORE Cycles: ", "BNEZ Cycles: "};
        String[] labelsGroup2 = {"ADD/SUB Stations: ", "MUL/DIV Stations: ", "LOAD/STORE Buffers: "};
        JTextField[] textFieldsGroup1 = new JTextField[7];
        JTextField[] textFieldsGroup2 = new JTextField[3];


        for (int i = 0; i < 7; i++) {
            JLabel label = new JLabel(labelsGroup1[i]);
            textFieldsGroup1[i] = new JTextField(20);
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.anchor = GridBagConstraints.LINE_END;
            panel.add(label, gbc);
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.LINE_START;
            panel.add(textFieldsGroup1[i], gbc);
        }


        for (int i = 0; i < 3; i++) {
            JLabel label = new JLabel(labelsGroup2[i]);
            textFieldsGroup2[i] = new JTextField(20);
            gbc.gridx = 2;
            gbc.gridy = i;
            gbc.anchor = GridBagConstraints.LINE_END;
            panel.add(label, gbc);
            gbc.gridx = 3;
            gbc.anchor = GridBagConstraints.LINE_START;
            panel.add(textFieldsGroup2[i], gbc);
        }


        String[] labelsGroup3 = new String[32];
        String[] labelsGroup4 = new String[32];
        JTextField[] textFieldsGroup3 = new JTextField[32];
        JTextField[] textFieldsGroup4 = new JTextField[32];


        for (int i = 0; i < 32; i++) {
            labelsGroup3[i] = "R" + i + ":";
            JLabel label = new JLabel(labelsGroup3[i]);
            textFieldsGroup3[i] = new JTextField(20);
            gbc.gridx = 0;
            gbc.gridy = i + 10;
            gbc.anchor = GridBagConstraints.LINE_END;
            panel.add(label, gbc);
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.LINE_START;
            panel.add(textFieldsGroup3[i], gbc);
        }


        for (int i = 0; i < 32; i++) {
            labelsGroup4[i] = "F" + i + ":";
            JLabel label = new JLabel(labelsGroup4[i]);
            textFieldsGroup4[i] = new JTextField(20);
            gbc.gridx = 2;
            gbc.gridy = i + 10;
            gbc.anchor = GridBagConstraints.LINE_END;
            panel.add(label, gbc);
            gbc.gridx = 3;
            gbc.anchor = GridBagConstraints.LINE_START;
            panel.add(textFieldsGroup4[i], gbc);
        }


        JLabel labelInstructionsFile = new JLabel("Instructions Text File Name (without an extension): ");
        JTextField textFieldInstructionsFile = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 73;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(labelInstructionsFile, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(textFieldInstructionsFile, gbc);


        JButton submitButton = new JButton("Run");
        submitButton.addActionListener(e -> {

            int addCycles = Integer.parseInt(textFieldsGroup1[0].getText());
            int subCycles = Integer.parseInt(textFieldsGroup1[1].getText());
            int mulCycles = Integer.parseInt(textFieldsGroup1[2].getText());
            int divCycles = Integer.parseInt(textFieldsGroup1[3].getText());
            int loadCycles = Integer.parseInt(textFieldsGroup1[4].getText());
            int storeCycles = Integer.parseInt(textFieldsGroup1[5].getText());
            int bnezCycles = Integer.parseInt(textFieldsGroup1[6].getText());
            int addSubStations = Integer.parseInt(textFieldsGroup2[0].getText());
            int mulDivStations = Integer.parseInt(textFieldsGroup2[1].getText());
            int loadStoreBuffers = Integer.parseInt(textFieldsGroup2[2].getText());

            String instructionsFile = textFieldInstructionsFile.getText();


            Tomasulo tomasulo = new Tomasulo();


            tomasulo.setAddCycles(addCycles);
            tomasulo.setSubCycles(subCycles);
            tomasulo.setMulCycles(mulCycles);
            tomasulo.setDivCycles(divCycles);
            tomasulo.setLoadCycles(loadCycles);
            tomasulo.setStoreCycles(storeCycles);
            tomasulo.setBNEZCycles(bnezCycles);
            tomasulo.setAddSubStations(addSubStations);
            tomasulo.setMulDivStations(mulDivStations);
            tomasulo.setLoadBuffers(loadStoreBuffers);
            tomasulo.setStoreBuffers(loadStoreBuffers);

            tomasulo.setInstructionFilePath(instructionsFile);

            Tomasulo.initializeTomasulo();

            for (int i = 0; i < 32; i++) {


                if (!textFieldsGroup3[i].getText().equals(""))
                    Tomasulo.getRegisterFile().setR(i, Long.parseLong(textFieldsGroup3[i].getText()));

                if (!textFieldsGroup4[i].getText().equals(""))
                    Tomasulo.getRegisterFile().setF(i, Double.parseDouble(textFieldsGroup4[i].getText()));
            }
            dispose();
            Tomasulo.simulate();

        });

        gbc.gridx = 0;
        gbc.gridy = 88;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(submitButton, gbc);


        add(panel);


        setVisible(true);
    }

    public static void processTomasuloInputs() {
        new TomasuloInputs();
    }
}