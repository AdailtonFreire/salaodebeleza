package com.mycompany.salaodebeleza;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServicoFrame {
    private JFrame frame;
    private JTextField descricaoField;
    private JTextField atendenteField;
    private JTextField valorField;
    private DatabaseManager dbManager;
    private DefaultTableModel tableModel;
    private JTable table;

    public ServicoFrame(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        frame = new JFrame("Cadastro de Serviços");
        frame.setSize(500, 300);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        inputPanel.add(new JLabel("Descrição:"));
        descricaoField = new JTextField();
        inputPanel.add(descricaoField);

        inputPanel.add(new JLabel("Atendente:"));
        atendenteField = new JTextField();
        inputPanel.add(atendenteField);

        inputPanel.add(new JLabel("Valor:"));
        valorField = new JTextField();
        inputPanel.add(valorField);

        JButton cadastrarButton = new JButton("Cadastrar");
        JButton consultarButton = new JButton("Consultar");
        JButton excluirButton = new JButton("Excluir");
        JButton atualizarButton = new JButton("Atualizar");
        inputPanel.add(cadastrarButton);
        inputPanel.add(consultarButton);
        inputPanel.add(excluirButton);
        inputPanel.add(atualizarButton);

        frame.add(inputPanel, BorderLayout.NORTH);

        // Tabela para mostrar os serviços
        tableModel = new DefaultTableModel(new String[]{"Descrição", "Atendente", "Valor"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Ação ao selecionar uma linha na tabela
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                    int selectedRow = table.getSelectedRow();
                    descricaoField.setText((String) tableModel.getValueAt(selectedRow, 0));
                    atendenteField.setText((String) tableModel.getValueAt(selectedRow, 1));
                    valorField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 2)));
                }
            }
        });

        cadastrarButton.addActionListener(e -> {
            String descricao = descricaoField.getText();
            String atendente = atendenteField.getText();
            double valor;

            try {
                valor = Double.parseDouble(valorField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Valor inválido. Insira um número válido.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Servico servico = new Servico(descricao, atendente, valor);
            dbManager.addServico(servico);
            JOptionPane.showMessageDialog(frame, "Serviço cadastrado: " + servico.getDescricao());
            clearFields();
            refreshTable(); // Atualiza a tabela após o cadastro
        });

        consultarButton.addActionListener(e -> refreshTable()); // Consulta sempre os dados mais recentes

        excluirButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String descricao = (String) tableModel.getValueAt(selectedRow, 0);
                dbManager.deleteServico(descricao);
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(frame, "Serviço excluído: " + descricao);
            } else {
                JOptionPane.showMessageDialog(frame, "Selecione um serviço para excluir.", "Erro", JOptionPane.WARNING_MESSAGE);
            }
        });

        atualizarButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String descricaoAntiga = (String) tableModel.getValueAt(selectedRow, 0);
                String novaDescricao = descricaoField.getText();
                String novoAtendente = atendenteField.getText();
                double novoValor;

                try {
                    novoValor = Double.parseDouble(valorField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Valor inválido. Insira um número válido.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Servico servicoAtualizado = new Servico(
                        novaDescricao.isEmpty() ? descricaoAntiga : novaDescricao,
                        novoAtendente.isEmpty() ? (String) tableModel.getValueAt(selectedRow, 1) : novoAtendente,
                        novoValor
                );

                dbManager.updateServico(descricaoAntiga, servicoAtualizado);
                tableModel.setValueAt(servicoAtualizado.getDescricao(), selectedRow, 0);
                tableModel.setValueAt(servicoAtualizado.getAtendente(), selectedRow, 1);
                tableModel.setValueAt(servicoAtualizado.getValor(), selectedRow, 2);

                JOptionPane.showMessageDialog(frame, "Serviço atualizado: " + servicoAtualizado.getDescricao());
                clearFields();
            } else {
                JOptionPane.showMessageDialog(frame, "Selecione um serviço para atualizar.", "Erro", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Chama refreshTable ao inicializar para mostrar os dados mais recentes
        refreshTable();
        frame.setVisible(true);
    }

    private void refreshTable() {
        // Limpa a tabela antes de preencher novamente
        tableModel.setRowCount(0);

        // Tenta obter os dados do banco de dados
        try (ResultSet resultSet = dbManager.getAllServicos()) {
            while (resultSet.next()) {
                String descricao = resultSet.getString("descricao");
                String atendente = resultSet.getString("atendente");
                double valor = resultSet.getDouble("valor");
                tableModel.addRow(new Object[]{descricao, atendente, valor});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void clearFields() {
        descricaoField.setText("");
        atendenteField.setText("");
        valorField.setText("");
    }
}
