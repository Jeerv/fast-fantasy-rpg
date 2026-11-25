package DAO;

import Model.*;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {

    private Inventario montar(ResultSet rs) throws SQLException {
        return new Inventario(rs.getInt("id_inventario"), rs.getInt("id_personagem"));
    }

    public void inserir(Inventario o) throws SQLException {
        String sql = "INSERT INTO inventario (id_personagem) VALUES (?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, o.getIdPersonagem());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) o.setIdInventario(keys.getInt(1));
            }
        }
    }

    public Inventario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM inventario WHERE id_inventario = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    public void atualizar(Inventario o) throws SQLException {
        String sql = "UPDATE inventario SET id_personagem = ? WHERE id_inventario = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, o.getIdPersonagem());
            ps.setInt(2, o.getIdInventario());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM inventario WHERE id_inventario = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Inventario> listarTodos() throws SQLException {
        List<Inventario> lista = new ArrayList<>();
        String sql = "SELECT * FROM inventario";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }

    public Inventario buscarPorPersonagem(int idPersonagem) throws SQLException {
        String sql = "SELECT * FROM inventario WHERE id_personagem = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPersonagem);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }
}
