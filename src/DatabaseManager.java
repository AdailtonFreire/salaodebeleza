package com.mycompany.salaodebeleza;

import java.sql.*;

public class DatabaseManager {
    private Connection connection;

    public DatabaseManager(String dbUrl) {
        try {
            connection = DriverManager.getConnection(dbUrl);
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            // Criação da tabela de clientes
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS clientes (nome TEXT PRIMARY KEY, email TEXT, telefone TEXT)");
            // Criação da tabela de serviços
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS servicos (descricao TEXT PRIMARY KEY, atendente TEXT, valor REAL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Métodos para Cliente
    public void addCliente(Cliente cliente) {
        try (PreparedStatement pstmt = connection.prepareStatement("INSERT INTO clientes (nome, email, telefone) VALUES (?, ?, ?)")) {
            pstmt.setString(1, cliente.getNome());
            pstmt.setString(2, cliente.getEmail());
            pstmt.setString(3, cliente.getTelefone());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCliente(String nomeAntigo, Cliente clienteAtualizado) {
        try (PreparedStatement pstmt = connection.prepareStatement("UPDATE clientes SET nome = ?, email = ?, telefone = ? WHERE nome = ?")) {
            pstmt.setString(1, clienteAtualizado.getNome());
            pstmt.setString(2, clienteAtualizado.getEmail());
            pstmt.setString(3, clienteAtualizado.getTelefone());
            pstmt.setString(4, nomeAntigo);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCliente(String nome) {
        try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM clientes WHERE nome = ?")) {
            pstmt.setString(1, nome);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getAllClientes() {
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery("SELECT * FROM clientes");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Métodos para Serviço
    public void addServico(Servico servico) {
        try (PreparedStatement pstmt = connection.prepareStatement("INSERT INTO servicos (descricao, atendente, valor) VALUES (?, ?, ?)")) {
            pstmt.setString(1, servico.getDescricao());
            pstmt.setString(2, servico.getAtendente());
            pstmt.setDouble(3, servico.getValor());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateServico(String descricaoAntiga, Servico servicoAtualizado) {
        try (PreparedStatement pstmt = connection.prepareStatement("UPDATE servicos SET descricao = ?, atendente = ?, valor = ? WHERE descricao = ?")) {
            pstmt.setString(1, servicoAtualizado.getDescricao());
            pstmt.setString(2, servicoAtualizado.getAtendente());
            pstmt.setDouble(3, servicoAtualizado.getValor());
            pstmt.setString(4, descricaoAntiga);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteServico(String descricao) {
        try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM servicos WHERE descricao = ?")) {
            pstmt.setString(1, descricao);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getAllServicos() {
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery("SELECT * FROM servicos");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
