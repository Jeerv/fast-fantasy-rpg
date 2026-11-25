package DAO;

import Model.*;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConquistaDAO {

    private Conquista montar(ResultSet rs) throws SQLException {
        return new Conquista(rs.getInt("id_conquista"), rs.getInt("moedas"), rs.getInt("id_cosmeticos"));
    }

    public void inserir(Conquista o) throws SQLException {
        String sql = "INSERT INTO conquista (moedas, id_cosmeticos) VALUES (?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, o.getMoedas());
            if (o.getIdCosmeticos() == 0) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, o.getIdCosmeticos());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) o.setIdConquista(keys.getInt(1));
            }
        }
    }

    public Conquista buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM conquista WHERE id_conquista = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    public void atualizar(Conquista o) throws SQLException {
        String sql = "UPDATE conquista SET moedas = ?, id_cosmeticos = ? WHERE id_conquista = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, o.getMoedas());
            if (o.getIdCosmeticos() == 0) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, o.getIdCosmeticos());
            ps.setInt(3, o.getIdConquista());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM conquista WHERE id_conquista = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Conquista> listarTodos() throws SQLException {
        List<Conquista> lista = new ArrayList<>();
        String sql = "SELECT * FROM conquista";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }
}
