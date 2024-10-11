package com.mycompany.salaodebeleza;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager("jdbc:sqlite:salon.db");

        SwingUtilities.invokeLater(() -> {
            new ClienteFrame(dbManager);
            new ServicoFrame(dbManager);
        });
    }
}
