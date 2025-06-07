import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class FormativeAssessment3GUI extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/fa3_assessment";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Narutostorm4";

    private JTextField fNameField;
    private JTextField lNameField;
    private JTextField destField;
    private JTextField phoneField;
    private JButton saveButton;
    private JButton viewButton;
    private JButton deleteButton;
    private JButton exitButton;
    private JTable passengerTable;
    private DefaultTableModel tableModel;
    private boolean isViewVisible = true;

    public FormativeAssessment3GUI() {
        setTitle("InterSA Passenger Details");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Input Panel with Form
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(inputPanel, BorderLayout.NORTH);

        inputPanel.add(new JLabel("First Name:"));
        fNameField = new JTextField();
        inputPanel.add(fNameField);

        inputPanel.add(new JLabel("Last Name:"));
        lNameField = new JTextField();
        inputPanel.add(lNameField);

        inputPanel.add(new JLabel("Destination:"));
        destField = new JTextField();
        inputPanel.add(destField);

        inputPanel.add(new JLabel("Phone Number:"));
        phoneField = new JTextField();
        inputPanel.add(phoneField);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        saveButton = new JButton("Insert");
        viewButton = new JButton("Hide");
        deleteButton = new JButton("Delete");
        exitButton = new JButton("Exit");
        buttonPanel.add(saveButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Table to Display Passenger Details
        tableModel = new DefaultTableModel(new String[]{"First Name", "Last Name", "Destination", "Phone Number"}, 0);
        passengerTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(passengerTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // Load passenger details by default
        loadPassengerDetails();

        // Add Action Listener to Save Button
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePassengerDetails();
            }
        });

        // Add Action Listener to View Button
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isViewVisible = !isViewVisible;
                tableScrollPane.setVisible(isViewVisible);
                if (isViewVisible) {
                    loadPassengerDetails();
                    viewButton.setText("Hide");
                } else {
                    viewButton.setText("View");
                }
            }
        });

        // Add Action Listener to Delete Button
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedPassenger();
            }
        });

        // Add Action Listener to Exit Button
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private void savePassengerDetails() {
        String firstName = fNameField.getText().trim();
        String lastName = lNameField.getText().trim();
        String destination = destField.getText().trim();
        String phoneNumber = phoneField.getText().trim();

        // Validate fields
        if (firstName.isEmpty() || lastName.isEmpty() || destination.isEmpty() || phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement insertStmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Check if the passenger already exists
            String checkSql = "SELECT COUNT(*) FROM passenger_details WHERE FName = ? AND LName = ? AND Dest = ? AND Phone = ?";
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, firstName);
            checkStmt.setString(2, lastName);
            checkStmt.setString(3, destination);
            checkStmt.setString(4, phoneNumber);

            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                // Passenger details already exist
                JOptionPane.showMessageDialog(this, "Passenger details already exist. Please re-enter different details.", "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
            } else {
                // Insert new passenger details
                String insertSql = "INSERT INTO passenger_details (FName, LName, Dest, Phone) VALUES (?, ?, ?, ?)";
                insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, firstName);
                insertStmt.setString(2, lastName);
                insertStmt.setString(3, destination);
                insertStmt.setString(4, phoneNumber);

                int rowsInserted = insertStmt.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Passenger details saved successfully!");
                    clearFields();

                    if (isViewVisible) {
                        tableModel.addRow(new Object[]{firstName, lastName, destination, phoneNumber});
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving passenger details: " + ex.getMessage());
        } finally {
            try {
                if (checkStmt != null) checkStmt.close();
                if (insertStmt != null) insertStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadPassengerDetails() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            stmt = conn.createStatement();
            String sql = "SELECT * FROM passenger_details";
            rs = stmt.executeQuery(sql);

            tableModel.setRowCount(0); // Clear existing data
            while (rs.next()) {
                String firstName = rs.getString("FName");
                String lastName = rs.getString("LName");
                String destination = rs.getString("Dest");
                String phoneNumber = rs.getString("Phone");

                tableModel.addRow(new Object[]{firstName, lastName, destination, phoneNumber});
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading passenger details: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void deleteSelectedPassenger() {
        int selectedRow = passengerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a passenger to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String firstName = (String) tableModel.getValueAt(selectedRow, 0);
        String lastName = (String) tableModel.getValueAt(selectedRow, 1);
        String destination = (String) tableModel.getValueAt(selectedRow, 2);
        String phoneNumber = (String) tableModel.getValueAt(selectedRow, 3);

        Connection conn = null;
        PreparedStatement deleteStmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String deleteSql = "DELETE FROM passenger_details WHERE FName = ? AND LName = ? AND Dest = ? AND Phone = ?";
            deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setString(1, firstName);
            deleteStmt.setString(2, lastName);
            deleteStmt.setString(3, destination);
            deleteStmt.setString(4, phoneNumber);

            int rowsDeleted = deleteStmt.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Passenger details deleted successfully!");
                tableModel.removeRow(selectedRow);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting passenger details: " + ex.getMessage());
        } finally {
            try {
                if (deleteStmt != null) deleteStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void clearFields() {
        fNameField.setText("");
        lNameField.setText("");
        destField.setText("");
        phoneField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormativeAssessment3GUI().setVisible(true));
    }
}
