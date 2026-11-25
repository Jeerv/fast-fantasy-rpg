package DAO;

import Model.*;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonagemMagiaDAO {

    private PersonagemMagia montar(ResultSet rs) throws SQLException {
        return new PersonagemMagia(rs.getInt("id_personagem"), rs.getInt("id_magia"));
    }

    public void inserir(PersonagemMagia o) throws SQLException {
        String sql = "INSERT INTO personagem_magia (id_personagem, id_magia) VALUES (?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, o.getIdPersonagem());
            ps.setInt(2, o.getIdMagia());
            ps.executeUpdate();
        }
    }

    public void deletar(int idPersonagem, int idMagia) throws SQLException {
        String sql = "DELETE FROM personagem_magia WHERE id_personagem = ? AND id_magia = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPersonagem);
            ps.setInt(2, idMagia);
            ps.executeUpdate();
        }
    }

    public List<PersonagemMagia> listarTodos() throws SQLException {
        List<PersonagemMagia> lista = new ArrayList<>();
        String sql = "SELECT * FROM personagem_magia";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }

    public List<PersonagemMagia> listarPorIdPersonagem(int idPersonagem) throws SQLException {
        List<PersonagemMagia> lista = new ArrayList<>();
        String sql = "SELECT * FROM personagem_magia WHERE id_personagem = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPersonagem);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        }
        return lista;
    }
}
