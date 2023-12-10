package view;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Tables {
    private static final ArrayList<ArrayList<JTable>> cyclesTables = new ArrayList<>();
    private static int currentCycleDisplayed = -1;
    private static JFrame frame = new JFrame("Tomasulo Simulator");
    private static JPanel panel = new JPanel();
    private static JScrollPane scrollPane;
    private static final String[] tableNames = {"Load Buffer", "Store Buffer", "Add/Sub Stations", "Mul/Div Stations", "Register File(Floating)", "Register File(Integer)", "Instruction Cache", "Data Cache"};
    private static final TableType[] tableTypes = {TableType.LOAD_BUFFER, TableType.STORE_BUFFER, TableType.ADD_SUB_STATIONS, TableType.MUL_DIV_STATIONS, TableType.REGISTER_FILE, TableType.REGISTER_FILE, TableType.INSTRUCTION_CACHE, TableType.DATA_CACHE};

    public enum TableType {
        DATA_CACHE,
        INSTRUCTION_CACHE,
        REGISTER_FILE,
        LOAD_BUFFER,
        STORE_BUFFER,
        ADD_SUB_STATIONS,
        MUL_DIV_STATIONS,
    }

    public static void addToTablesBuffer(String[][] data,TableType type, int cycle) {
        if (cyclesTables.size() <= cycle) {
            cyclesTables.add(new ArrayList<>());
        }
        cyclesTables.get(cycle).add(createTable( data, type));
    }
    public static void displayAllTables() {
        // Create JFrame and add the scrollPane to it
        frame = new JFrame("Tomasulo Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        viewNextTables();
        frame.pack();
        frame.setVisible(true);
    }
    private static void viewNextTables() {
        if (currentCycleDisplayed >= cyclesTables.size()-1) {
            return;
        }
        currentCycleDisplayed = Math.min(cyclesTables.size()-1, Math.max(0, ++currentCycleDisplayed));
        if (currentCycleDisplayed >= 0 && scrollPane != null ){
            frame.remove(scrollPane);
        }
        // Create a JPanel with BoxLayout
        panel.removeAll();

        JPanel status = new JPanel();
        JLabel label = new JLabel("Cycle " + currentCycleDisplayed + "/" + (cyclesTables.size() - 1));
        label.setFont(new Font("Arial", Font.BOLD, 24));
        // Create buttons with action listeners
        JButton next = new JButton("Next" );
        next.setFont(new Font("Arial", Font.BOLD, 24));
        next.addActionListener(e -> viewNextTables());

        JButton prev = new JButton("Prev");
        prev.setFont(new Font("Arial", Font.BOLD, 24));
        prev.addActionListener(e -> viewPreviousTables());

        JButton exit = new JButton("Exit");
        exit.setBackground(Color.RED);
        exit.setFont(new Font("Arial", Font.BOLD, 24));
        exit.addActionListener(e -> System.exit(0));

        status.add(prev);
        status.add(label);
        status.add(next);
        status.add(exit);
        panel.add(status);

        // Create tables and add them to the panel
        for (int i = 0; i < cyclesTables.get(currentCycleDisplayed).size(); i++) {
            // add titles only table
            JTable titles = new JTable(new String[][]{getColumnNames(tableTypes[i])},getColumnNames(tableTypes[i]));
            titles.setRowHeight(30);
            titles.getColumnModel().getColumn(0).setMaxWidth(60);
            titles.setFont(new Font("Times New Roman", Font.BOLD, 11));
            titles.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
            titles.getTableHeader().setReorderingAllowed(false);
            titles.getTableHeader().setResizingAllowed(true);
            titles.setShowGrid(false);
            titles.setShowHorizontalLines(true);
            titles.setShowVerticalLines(false);
            titles.setFillsViewportHeight(true);
            // Create label
            JLabel title = new JLabel(tableNames[i], SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 18));
            panel.add(title);
            panel.add(titles);

            // Create table
            JTable table = cyclesTables.get(currentCycleDisplayed).get(i);
            panel.add(table);
        }

        // Create JScrollPane and add the panel to it
        scrollPane = new JScrollPane(panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(8);

        // Create JFrame and add the scrollPane to it
        frame.add(scrollPane);
        frame.revalidate();
        frame.repaint();
    }
    private static void viewPreviousTables(){
        currentCycleDisplayed = Math.max(-1, currentCycleDisplayed - 2);
        viewNextTables();
    }

    private static JTable createTable(String[][] data,TableType type ) {
        // Column Names
        String[] columnNames;

        // Data
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if ((data[i][j] == null) || (data[i][j].isEmpty())) {
                    data[i][j] = "--";
                }
            }
        }

        columnNames = getColumnNames(type);

        // Create table
        JTable table = new JTable(data, columnNames);

        // Set table properties
        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.setFont(new Font("Arial", Font.PLAIN, 18));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 20));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(true);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);

        return table;
    }

    public static String[] getColumnNames(TableType type) {
        return switch (type) {
            case DATA_CACHE -> new String[]{"Address", "Data"};
            case INSTRUCTION_CACHE -> new String[]{"Address", "Instruction"};
            case REGISTER_FILE -> new String[]{"Reg Name", "Reg Status(Q)", "Value(V)"};
            case LOAD_BUFFER -> new String[]{"Index", "Busy", "Address", "V", "Q", "Dest"};
            case STORE_BUFFER -> new String[]{"Index", "Busy", "Address", "V", "Q", "Src"};
            case ADD_SUB_STATIONS, MUL_DIV_STATIONS -> new String[]{"Index", "Busy", "Op", "Vj", "Vk", "Qj", "Qk"};
        };
    }
    public static void main(String[] args) {
        // Create a JPanel with BoxLayout
JPanel panel = new JPanel();
panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

// Create tables and add them to the panel
for (int i = 0; i < 6; i++) {
    // Create label
    JLabel label = new JLabel("Table " + (i + 1));
    panel.add(label);

    // Create table
    String[] columnNames = {"Column 1", "Column 2", "Column 3"};
    String[][] data = {
            {"Value 1", "Value 2", "Value 3"},
            {"Value 4", "Value 5", "Value 6"},
            {"Value 7", "Value 8", "Value 9"}
    };
    JTable table = new JTable(data, columnNames);
    panel.add(table);
}

// Create JScrollPane and add the panel to it
JScrollPane scrollPane = new JScrollPane(panel);

// Create JFrame and add the scrollPane to it
JFrame frame = new JFrame();
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
frame.add(scrollPane);
frame.pack();
frame.setVisible(true);
    }
}
