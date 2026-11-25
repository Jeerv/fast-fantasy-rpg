package DAO;

import Model.*;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PericiaDAO {

    private Pericia montar(ResultSet rs) throws SQLException {
        return new Pericia(rs.getInt("id_pericia"), rs.getString("nome"), Atributo.doBanco(rs.getString("atributo")));
    }

    public void inserir(Pericia o) throws SQLException {
        String sql = "INSERT INTO pericia (nome, atributo) VALUES (?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, o.getNome());
            ps.setString(2, o.getAtributo().getValorBanco());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) o.setIdPericia(keys.getInt(1));
            }
        }
    }

    public Pericia buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM pericia WHERE id_pericia = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    public void atualizar(Pericia o) throws SQLException {
        String sql = "UPDATE pericia SET nome = ?, atributo = ? WHERE id_pericia = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, o.getNome());
            ps.setString(2, o.getAtributo().getValorBanco());
            ps.setInt(3, o.getIdPericia());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM pericia WHERE id_pericia = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Pericia> listarTodos() throws SQLException {
        List<Pericia> lista = new ArrayList<>();
        String sql = "SELECT * FROM pericia";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }
}
