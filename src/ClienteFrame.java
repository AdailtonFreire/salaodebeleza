package com.mycompany.salaodebeleza;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteFrame {
    private JFrame frame;
    private JTextField nomeField;
    private JTextField emailField;
    private JTextField telefoneField;
    private DatabaseManager dbManager;
    private DefaultTableModel tableModel;
    private JTable table;

    public ClienteFrame(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        frame = new JFrame("Cadastro de Clientes");
        frame.setSize(500, 300);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        inputPanel.add(new JLabel("Nome:"));
        nomeField = new JTextField();
        inputPanel.add(nomeField);

        inputPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        inputPanel.add(emailField);

        inputPanel.add(new JLabel("Telefone:"));
        telefoneField = new JTextField();
        inputPanel.add(telefoneField);

        JButton cadastrarButton = new JButton("Cadastrar");
        JButton consultarButton = new JButton("Consultar");
        JButton excluirButton = new JButton("Excluir");
        JButton atualizarButton = new JButton("Atualizar");
        inputPanel.add(cadastrarButton);
        inputPanel.add(consultarButton);
        inputPanel.add(excluirButton);
        inputPanel.add(atualizarButton);

        frame.add(inputPanel, BorderLayout.NORTH);

        // Tabela para mostrar os clientes
        tableModel = new DefaultTableModel(new String[]{"Nome", "Email", "Telefone"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Ação ao selecionar uma linha na tabela
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                    int selectedRow = table.getSelectedRow();
                    nomeField.setText((String) tableModel.getValueAt(selectedRow, 0));
                    emailField.setText((String) tableModel.getValueAt(selectedRow, 1));
                    telefoneField.setText((String) tableModel.getValueAt(selectedRow, 2));
                }
            }
        });

        cadastrarButton.addActionListener(e -> {
            String nome = nomeField.getText();
            String email = emailField.getText();
            String telefone = telefoneField.getText();
            Cliente cliente = new Cliente(nome, email, telefone);
            dbManager.addCliente(cliente);
            JOptionPane.showMessageDialog(frame, "Cliente cadastrado: " + cliente.getNome());
            clearFields();
        });

        consultarButton.addActionListener(e -> refreshTable());

        excluirButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String nome = (String) tableModel.getValueAt(selectedRow, 0);
                dbManager.deleteCliente(nome);
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(frame, "Cliente excluído: " + nome);
            } else {
                JOptionPane.showMessageDialog(frame, "Selecione um cliente para excluir.", "Erro", JOptionPane.WARNING_MESSAGE);
            }
        });

        atualizarButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String nomeAntigo = (String) tableModel.getValueAt(selectedRow, 0);
                String novoNome = nomeField.getText();
                String novoEmail = emailField.getText();
                String novoTelefone = telefoneField.getText();

                Cliente clienteAtualizado = new Cliente(
                        novoNome.isEmpty() ? nomeAntigo : novoNome,
                        novoEmail.isEmpty() ? (String) tableModel.getValueAt(selectedRow, 1) : novoEmail,
                        novoTelefone.isEmpty() ? (String) tableModel.getValueAt(selectedRow, 2) : novoTelefone
                );

                dbManager.updateCliente(nomeAntigo, clienteAtualizado);
                tableModel.setValueAt(clienteAtualizado.getNome(), selectedRow, 0);
                tableModel.setValueAt(clienteAtualizado.getEmail(), selectedRow, 1);
                tableModel.setValueAt(clienteAtualizado.getTelefone(), selectedRow, 2);

                JOptionPane.showMessageDialog(frame, "Cliente atualizado: " + clienteAtualizado.getNome());
                clearFields();
            } else {
                JOptionPane.showMessageDialog(frame, "Selecione um cliente para atualizar.", "Erro", JOptionPane.WARNING_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    private void refreshTable() {
        ResultSet resultSet = dbManager.getAllClientes();
        tableModel.setRowCount(0);
        try {
            while (resultSet.next()) {
                String nome = resultSet.getString("nome");
                String email = resultSet.getString("email");
                String telefone = resultSet.getString("telefone");
                tableModel.addRow(new Object[]{nome, email, telefone});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void clearFields() {
        nomeField.setText("");
        emailField.setText("");
        telefoneField.setText("");
    }
}
