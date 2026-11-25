package DAO;

import Model.*;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CosmeticosDAO {

    private Cosmeticos montar(ResultSet rs) throws SQLException {
        return new Cosmeticos(rs.getInt("id_cosmeticos"), rs.getInt("tokens"), rs.getInt("icones_de_perfil"), rs.getInt("dados_personalizados"));
    }

    public void inserir(Cosmeticos o) throws SQLException {
        String sql = "INSERT INTO cosmeticos (tokens, icones_de_perfil, dados_personalizados) VALUES (?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, o.getTokens());
            ps.setInt(2, o.getIconesDePerfil());
            ps.setInt(3, o.getDadosPersonalizados());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) o.setIdCosmeticos(keys.getInt(1));
            }
        }
    }

    public Cosmeticos buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM cosmeticos WHERE id_cosmeticos = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    public void atualizar(Cosmeticos o) throws SQLException {
        String sql = "UPDATE cosmeticos SET tokens = ?, icones_de_perfil = ?, dados_personalizados = ? WHERE id_cosmeticos = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, o.getTokens());
            ps.setInt(2, o.getIconesDePerfil());
            ps.setInt(3, o.getDadosPersonalizados());
            ps.setInt(4, o.getIdCosmeticos());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM cosmeticos WHERE id_cosmeticos = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Cosmeticos> listarTodos() throws SQLException {
        List<Cosmeticos> lista = new ArrayList<>();
        String sql = "SELECT * FROM cosmeticos";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }
}
