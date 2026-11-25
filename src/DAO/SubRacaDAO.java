package DAO;

import Model.*;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubRacaDAO {

    private SubRaca montar(ResultSet rs) throws SQLException {
        return new SubRaca(rs.getInt("id_sub_raca"), rs.getString("nome"), rs.getString("descricao"), rs.getInt("id_raca"));
    }

    public void inserir(SubRaca o) throws SQLException {
        String sql = "INSERT INTO sub_raca (nome, descricao, id_raca) VALUES (?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, o.getNome());
            ps.setString(2, o.getDescricao());
            ps.setInt(3, o.getIdRaca());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) o.setIdSubRaca(keys.getInt(1));
            }
        }
    }

    public SubRaca buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM sub_raca WHERE id_sub_raca = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    public void atualizar(SubRaca o) throws SQLException {
        String sql = "UPDATE sub_raca SET nome = ?, descricao = ?, id_raca = ? WHERE id_sub_raca = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, o.getNome());
            ps.setString(2, o.getDescricao());
            ps.setInt(3, o.getIdRaca());
            ps.setInt(4, o.getIdSubRaca());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM sub_raca WHERE id_sub_raca = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<SubRaca> listarTodos() throws SQLException {
        List<SubRaca> lista = new ArrayList<>();
        String sql = "SELECT * FROM sub_raca";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }

    public List<SubRaca> listarPorIdRaca(int idRaca) throws SQLException {
        List<SubRaca> lista = new ArrayList<>();
        String sql = "SELECT * FROM sub_raca WHERE id_raca = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idRaca);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        }
        return lista;
    }
}
