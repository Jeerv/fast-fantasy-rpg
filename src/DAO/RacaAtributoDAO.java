package DAO;

import Model.*;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RacaAtributoDAO {

    private RacaAtributo montar(ResultSet rs) throws SQLException {
        return new RacaAtributo(rs.getInt("id_raca"), Atributo.doBanco(rs.getString("atributo")), rs.getInt("bonus"));
    }

    public void inserir(RacaAtributo o) throws SQLException {
        String sql = "INSERT INTO raca_atributo (id_raca, atributo, bonus) VALUES (?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, o.getIdRaca());
            ps.setString(2, o.getAtributo().getValorBanco());
            ps.setInt(3, o.getBonus());
            ps.executeUpdate();
        }
    }

    public void atualizar(RacaAtributo o) throws SQLException {
        String sql = "UPDATE raca_atributo SET bonus = ? WHERE id_raca = ? AND atributo = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, o.getBonus());
            ps.setInt(2, o.getIdRaca());
            ps.setString(3, o.getAtributo().getValorBanco());
            ps.executeUpdate();
        }
    }

    public void deletar(int idRaca, Atributo atributo) throws SQLException {
        String sql = "DELETE FROM raca_atributo WHERE id_raca = ? AND atributo = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idRaca);
            ps.setString(2, atributo.getValorBanco());
            ps.executeUpdate();
        }
    }

    public List<RacaAtributo> listarTodos() throws SQLException {
        List<RacaAtributo> lista = new ArrayList<>();
        String sql = "SELECT * FROM raca_atributo";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }

    public List<RacaAtributo> listarPorIdRaca(int idRaca) throws SQLException {
        List<RacaAtributo> lista = new ArrayList<>();
        String sql = "SELECT * FROM raca_atributo WHERE id_raca = ?";
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
