package DAO;

import Model.Mesa;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MesaDAO {

    public void inserir(Mesa mesa) {
        String sql = "INSERT INTO mesa (mesa_privada, status, nome, descricao, codigo_convite, id_mestre) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, mesa.isMesaPrivada());
            stmt.setString(2, mesa.getStatus());
            stmt.setString(3, mesa.getNome());
            stmt.setString(4, mesa.getDescricao());
            stmt.setString(5, mesa.getCodigoConvite());
            stmt.setInt(6, mesa.getIdMestre());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir mesa: " + e.getMessage(), e);
        }
    }

    public List<Mesa> listar() {
        List<Mesa> mesas = new ArrayList<>();
        String sql = "SELECT * FROM mesa";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Mesa m = new Mesa();
                m.setIdMesa(rs.getInt("id_mesa"));
                m.setMesaPrivada(rs.getBoolean("mesa_privada"));
                m.setStatus(rs.getString("status"));
                m.setNome(rs.getString("nome"));
                m.setDescricao(rs.getString("descricao"));
                m.setCodigoConvite(rs.getString("codigo_convite"));
                m.setIdMestre(rs.getInt("id_mestre"));
                mesas.add(m);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar mesas: " + e.getMessage(), e);
        }
        return mesas;
    }

    public void atualizar(Mesa mesa) {
        String sql = "UPDATE mesa SET nome = ?, descricao = ?, status = ? WHERE id_mesa = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mesa.getNome());
            stmt.setString(2, mesa.getDescricao());
            stmt.setString(3, mesa.getStatus());
            stmt.setInt(4, mesa.getIdMesa());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar mesa: " + e.getMessage(), e);
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM mesa WHERE id_mesa = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar mesa (verifique se há personagens vinculados): " + e.getMessage(), e);
        }
    }
}
