package DAO;

import Model.*;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioItemDAO {

    private InventarioItem montar(ResultSet rs) throws SQLException {
        return new InventarioItem(rs.getInt("id_inventario"), rs.getInt("id_item"), rs.getInt("quantidade"));
    }

    public void inserir(InventarioItem o) throws SQLException {
        String sql = "INSERT INTO inventario_item (id_inventario, id_item, quantidade) VALUES (?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, o.getIdInventario());
            ps.setInt(2, o.getIdItem());
            ps.setInt(3, o.getQuantidade());
            ps.executeUpdate();
        }
    }

    public void atualizar(InventarioItem o) throws SQLException {
        String sql = "UPDATE inventario_item SET quantidade = ? WHERE id_inventario = ? AND id_item = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, o.getQuantidade());
            ps.setInt(2, o.getIdInventario());
            ps.setInt(3, o.getIdItem());
            ps.executeUpdate();
        }
    }

    public void deletar(int idInventario, int idItem) throws SQLException {
        String sql = "DELETE FROM inventario_item WHERE id_inventario = ? AND id_item = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idInventario);
            ps.setInt(2, idItem);
            ps.executeUpdate();
        }
    }

    public List<InventarioItem> listarTodos() throws SQLException {
        List<InventarioItem> lista = new ArrayList<>();
        String sql = "SELECT * FROM inventario_item";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }

    public List<InventarioItem> listarPorIdInventario(int idInventario) throws SQLException {
        List<InventarioItem> lista = new ArrayList<>();
        String sql = "SELECT * FROM inventario_item WHERE id_inventario = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idInventario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        }
        return lista;
    }
}
