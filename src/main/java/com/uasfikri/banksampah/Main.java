package com.uasfikri.banksampah;

import com.formdev.flatlaf.FlatDarkLaf;
import com.uasfikri.banksampah.ui.MainFrame;
import com.uasfikri.banksampah.util.DatabaseHelper;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Initialize SQLite Database and create tables if they do not exist
        DatabaseHelper.initializeDatabase();

        // Setup FlatLaf Dark Look and Feel
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            System.out.println("FlatLaf Dark Theme loaded successfully.");
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf Look and Feel. Falling back to default.");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Adjust some Swing components styles for customized modern look
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("TableHeader.background", new java.awt.Color(40, 40, 50));
        UIManager.put("Table.selectionBackground", new java.awt.Color(46, 204, 113, 60)); // Soft green highlight
        UIManager.put("Table.selectionForeground", java.awt.Color.WHITE);

        // Start Application in Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
