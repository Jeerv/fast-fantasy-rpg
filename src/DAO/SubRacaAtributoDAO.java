package DAO;

import Model.*;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubRacaAtributoDAO {

    private SubRacaAtributo montar(ResultSet rs) throws SQLException {
        return new SubRacaAtributo(rs.getInt("id_sub_raca"), Atributo.doBanco(rs.getString("atributo")), rs.getInt("bonus"));
    }

    public void inserir(SubRacaAtributo o) throws SQLException {
        String sql = "INSERT INTO sub_raca_atributo (id_sub_raca, atributo, bonus) VALUES (?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, o.getIdSubRaca());
            ps.setString(2, o.getAtributo().getValorBanco());
            ps.setInt(3, o.getBonus());
            ps.executeUpdate();
        }
    }

    public void atualizar(SubRacaAtributo o) throws SQLException {
        String sql = "UPDATE sub_raca_atributo SET bonus = ? WHERE id_sub_raca = ? AND atributo = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, o.getBonus());
            ps.setInt(2, o.getIdSubRaca());
            ps.setString(3, o.getAtributo().getValorBanco());
            ps.executeUpdate();
        }
    }

    public void deletar(int idSubRaca, Atributo atributo) throws SQLException {
        String sql = "DELETE FROM sub_raca_atributo WHERE id_sub_raca = ? AND atributo = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSubRaca);
            ps.setString(2, atributo.getValorBanco());
            ps.executeUpdate();
        }
    }

    public List<SubRacaAtributo> listarTodos() throws SQLException {
        List<SubRacaAtributo> lista = new ArrayList<>();
        String sql = "SELECT * FROM sub_raca_atributo";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }

    public List<SubRacaAtributo> listarPorIdSubRaca(int idSubRaca) throws SQLException {
        List<SubRacaAtributo> lista = new ArrayList<>();
        String sql = "SELECT * FROM sub_raca_atributo WHERE id_sub_raca = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSubRaca);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        }
        return lista;
    }
}
