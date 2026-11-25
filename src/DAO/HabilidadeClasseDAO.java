package DAO;

import Model.*;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HabilidadeClasseDAO {

    private HabilidadeClasse montar(ResultSet rs) throws SQLException {
        return new HabilidadeClasse(rs.getInt("id_habilidade_classe"), rs.getString("nome"), rs.getString("descricao"), rs.getInt("nivel"), rs.getInt("id_classe"));
    }

    public void inserir(HabilidadeClasse o) throws SQLException {
        String sql = "INSERT INTO habilidade_classe (nome, descricao, nivel, id_classe) VALUES (?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, o.getNome());
            ps.setString(2, o.getDescricao());
            ps.setInt(3, o.getNivel());
            ps.setInt(4, o.getIdClasse());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) o.setIdHabilidadeClasse(keys.getInt(1));
            }
        }
    }

    public HabilidadeClasse buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM habilidade_classe WHERE id_habilidade_classe = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    public void atualizar(HabilidadeClasse o) throws SQLException {
        String sql = "UPDATE habilidade_classe SET nome = ?, descricao = ?, nivel = ?, id_classe = ? WHERE id_habilidade_classe = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, o.getNome());
            ps.setString(2, o.getDescricao());
            ps.setInt(3, o.getNivel());
            ps.setInt(4, o.getIdClasse());
            ps.setInt(5, o.getIdHabilidadeClasse());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM habilidade_classe WHERE id_habilidade_classe = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<HabilidadeClasse> listarTodos() throws SQLException {
        List<HabilidadeClasse> lista = new ArrayList<>();
        String sql = "SELECT * FROM habilidade_classe";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }

    public List<HabilidadeClasse> listarPorIdClasse(int idClasse) throws SQLException {
        List<HabilidadeClasse> lista = new ArrayList<>();
        String sql = "SELECT * FROM habilidade_classe WHERE id_classe = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idClasse);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        }
        return lista;
    }

    // Habilidades que o personagem já tem: as da classe com nivel <= nivel do personagem.
    public List<HabilidadeClasse> listarDisponiveis(int idClasse, int nivel) throws SQLException {
        List<HabilidadeClasse> lista = new ArrayList<>();
        String sql = "SELECT * FROM habilidade_classe WHERE id_classe = ? AND nivel <= ? ORDER BY nivel, nome";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idClasse);
            ps.setInt(2, nivel);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        }
        return lista;
    }
}
