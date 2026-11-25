package DAO;

import Model.*;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubclasseDAO {

    private Subclasse montar(ResultSet rs) throws SQLException {
        return new Subclasse(rs.getInt("id_subclasse"), rs.getString("nome"), rs.getString("descricao"), rs.getInt("id_classe"));
    }

    public void inserir(Subclasse o) throws SQLException {
        String sql = "INSERT INTO subclasse (nome, descricao, id_classe) VALUES (?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, o.getNome());
            ps.setString(2, o.getDescricao());
            ps.setInt(3, o.getIdClasse());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) o.setIdSubclasse(keys.getInt(1));
            }
        }
    }

    public Subclasse buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM subclasse WHERE id_subclasse = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    public void atualizar(Subclasse o) throws SQLException {
        String sql = "UPDATE subclasse SET nome = ?, descricao = ?, id_classe = ? WHERE id_subclasse = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, o.getNome());
            ps.setString(2, o.getDescricao());
            ps.setInt(3, o.getIdClasse());
            ps.setInt(4, o.getIdSubclasse());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM subclasse WHERE id_subclasse = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Subclasse> listarTodos() throws SQLException {
        List<Subclasse> lista = new ArrayList<>();
        String sql = "SELECT * FROM subclasse";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }

    public List<Subclasse> listarPorIdClasse(int idClasse) throws SQLException {
        List<Subclasse> lista = new ArrayList<>();
        String sql = "SELECT * FROM subclasse WHERE id_classe = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idClasse);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        }
        return lista;
    }
}
