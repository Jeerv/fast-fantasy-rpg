package DAO;

import Model.*;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AtributosDAO {

    private Atributos montar(ResultSet rs) throws SQLException {
        return new Atributos(rs.getInt("id_atributos"), rs.getInt("forca"), rs.getInt("destreza"), rs.getInt("constituicao"), rs.getInt("inteligencia"), rs.getInt("sabedoria"), rs.getInt("carisma"));
    }

    public void inserir(Atributos o) throws SQLException {
        String sql = "INSERT INTO atributos (forca, destreza, constituicao, inteligencia, sabedoria, carisma) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, o.getForca());
            ps.setInt(2, o.getDestreza());
            ps.setInt(3, o.getConstituicao());
            ps.setInt(4, o.getInteligencia());
            ps.setInt(5, o.getSabedoria());
            ps.setInt(6, o.getCarisma());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) o.setIdAtributos(keys.getInt(1));
            }
        }
    }

    public Atributos buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM atributos WHERE id_atributos = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    public void atualizar(Atributos o) throws SQLException {
        String sql = "UPDATE atributos SET forca = ?, destreza = ?, constituicao = ?, inteligencia = ?, sabedoria = ?, carisma = ? WHERE id_atributos = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, o.getForca());
            ps.setInt(2, o.getDestreza());
            ps.setInt(3, o.getConstituicao());
            ps.setInt(4, o.getInteligencia());
            ps.setInt(5, o.getSabedoria());
            ps.setInt(6, o.getCarisma());
            ps.setInt(7, o.getIdAtributos());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM atributos WHERE id_atributos = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Atributos> listarTodos() throws SQLException {
        List<Atributos> lista = new ArrayList<>();
        String sql = "SELECT * FROM atributos";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }
}
