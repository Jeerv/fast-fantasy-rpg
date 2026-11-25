package DAO;

import Model.*;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HabilidadeSubclasseDAO {

    private HabilidadeSubclasse montar(ResultSet rs) throws SQLException {
        return new HabilidadeSubclasse(rs.getInt("id_habilidade_subclasse"), rs.getString("nome"), rs.getString("descricao"), rs.getInt("nivel"), rs.getInt("id_subclasse"));
    }

    public void inserir(HabilidadeSubclasse o) throws SQLException {
        String sql = "INSERT INTO habilidade_subclasse (nome, descricao, nivel, id_subclasse) VALUES (?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, o.getNome());
            ps.setString(2, o.getDescricao());
            ps.setInt(3, o.getNivel());
            ps.setInt(4, o.getIdSubclasse());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) o.setIdHabilidadeSubclasse(keys.getInt(1));
            }
        }
    }

    public HabilidadeSubclasse buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM habilidade_subclasse WHERE id_habilidade_subclasse = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    public void atualizar(HabilidadeSubclasse o) throws SQLException {
        String sql = "UPDATE habilidade_subclasse SET nome = ?, descricao = ?, nivel = ?, id_subclasse = ? WHERE id_habilidade_subclasse = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, o.getNome());
            ps.setString(2, o.getDescricao());
            ps.setInt(3, o.getNivel());
            ps.setInt(4, o.getIdSubclasse());
            ps.setInt(5, o.getIdHabilidadeSubclasse());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM habilidade_subclasse WHERE id_habilidade_subclasse = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<HabilidadeSubclasse> listarTodos() throws SQLException {
        List<HabilidadeSubclasse> lista = new ArrayList<>();
        String sql = "SELECT * FROM habilidade_subclasse";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }

    public List<HabilidadeSubclasse> listarPorIdSubclasse(int idSubclasse) throws SQLException {
        List<HabilidadeSubclasse> lista = new ArrayList<>();
        String sql = "SELECT * FROM habilidade_subclasse WHERE id_subclasse = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSubclasse);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        }
        return lista;
    }

    // Habilidades que o personagem já tem: as da subclasse com nivel <= nivel do personagem.
    public List<HabilidadeSubclasse> listarDisponiveis(int idSubclasse, int nivel) throws SQLException {
        List<HabilidadeSubclasse> lista = new ArrayList<>();
        String sql = "SELECT * FROM habilidade_subclasse WHERE id_subclasse = ? AND nivel <= ? ORDER BY nivel, nome";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSubclasse);
            ps.setInt(2, nivel);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        }
        return lista;
    }
}
