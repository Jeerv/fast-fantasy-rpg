package DAO;

import Model.*;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClasseDAO {

    private Classe montar(ResultSet rs) throws SQLException {
        return new Classe(rs.getInt("id_classe"), rs.getString("nome"), rs.getString("descricao"));
    }

    public void inserir(Classe o) throws SQLException {
        String sql = "INSERT INTO classe (nome, descricao) VALUES (?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, o.getNome());
            ps.setString(2, o.getDescricao());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) o.setIdClasse(keys.getInt(1));
            }
        }
    }

    public Classe buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM classe WHERE id_classe = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    public void atualizar(Classe o) throws SQLException {
        String sql = "UPDATE classe SET nome = ?, descricao = ? WHERE id_classe = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, o.getNome());
            ps.setString(2, o.getDescricao());
            ps.setInt(3, o.getIdClasse());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM classe WHERE id_classe = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Classe> listarTodos() throws SQLException {
        List<Classe> lista = new ArrayList<>();
        String sql = "SELECT * FROM classe";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }
}
