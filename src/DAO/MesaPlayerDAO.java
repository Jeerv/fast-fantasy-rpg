package DAO;

import Model.*;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MesaPlayerDAO {

    private MesaPlayer montar(ResultSet rs) throws SQLException {
        return new MesaPlayer(rs.getInt("id_mesa"), rs.getInt("id_player"));
    }

    public void inserir(MesaPlayer o) throws SQLException {
        String sql = "INSERT INTO mesa_player (id_mesa, id_player) VALUES (?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, o.getIdMesa());
            ps.setInt(2, o.getIdPlayer());
            ps.executeUpdate();
        }
    }

    public void deletar(int idMesa, int idPlayer) throws SQLException {
        String sql = "DELETE FROM mesa_player WHERE id_mesa = ? AND id_player = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMesa);
            ps.setInt(2, idPlayer);
            ps.executeUpdate();
        }
    }

    public List<MesaPlayer> listarTodos() throws SQLException {
        List<MesaPlayer> lista = new ArrayList<>();
        String sql = "SELECT * FROM mesa_player";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }

    public List<MesaPlayer> listarPorIdMesa(int idMesa) throws SQLException {
        List<MesaPlayer> lista = new ArrayList<>();
        String sql = "SELECT * FROM mesa_player WHERE id_mesa = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMesa);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        }
        return lista;
    }

    public List<MesaPlayer> listarPorIdPlayer(int idPlayer) throws SQLException {
        List<MesaPlayer> lista = new ArrayList<>();
        String sql = "SELECT * FROM mesa_player WHERE id_player = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPlayer);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        }
        return lista;
    }
}
