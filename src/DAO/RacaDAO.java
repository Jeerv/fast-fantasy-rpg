package DAO;

import Model.*;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RacaDAO {

    private Raca montar(ResultSet rs) throws SQLException {
        return new Raca(rs.getInt("id_raca"), rs.getString("nome"), rs.getString("descricao"));
    }

    public void inserir(Raca o) throws SQLException {
        String sql = "INSERT INTO raca (nome, descricao) VALUES (?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, o.getNome());
            ps.setString(2, o.getDescricao());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) o.setIdRaca(keys.getInt(1));
            }
        }
    }

    public Raca buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM raca WHERE id_raca = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    public void atualizar(Raca o) throws SQLException {
        String sql = "UPDATE raca SET nome = ?, descricao = ? WHERE id_raca = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, o.getNome());
            ps.setString(2, o.getDescricao());
            ps.setInt(3, o.getIdRaca());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM raca WHERE id_raca = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Raca> listarTodos() throws SQLException {
        List<Raca> lista = new ArrayList<>();
        String sql = "SELECT * FROM raca";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }
}
