package DAO;

import Model.*;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonagemPericiaDAO {

    private PersonagemPericia montar(ResultSet rs) throws SQLException {
        return new PersonagemPericia(rs.getInt("id_personagem"), rs.getInt("id_pericia"), rs.getBoolean("proficiente"));
    }

    public void inserir(PersonagemPericia o) throws SQLException {
        String sql = "INSERT INTO personagem_pericia (id_personagem, id_pericia, proficiente) VALUES (?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, o.getIdPersonagem());
            ps.setInt(2, o.getIdPericia());
            ps.setBoolean(3, o.isProficiente());
            ps.executeUpdate();
        }
    }

    public void atualizar(PersonagemPericia o) throws SQLException {
        String sql = "UPDATE personagem_pericia SET proficiente = ? WHERE id_personagem = ? AND id_pericia = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, o.isProficiente());
            ps.setInt(2, o.getIdPersonagem());
            ps.setInt(3, o.getIdPericia());
            ps.executeUpdate();
        }
    }

    public void deletar(int idPersonagem, int idPericia) throws SQLException {
        String sql = "DELETE FROM personagem_pericia WHERE id_personagem = ? AND id_pericia = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPersonagem);
            ps.setInt(2, idPericia);
            ps.executeUpdate();
        }
    }

    public List<PersonagemPericia> listarTodos() throws SQLException {
        List<PersonagemPericia> lista = new ArrayList<>();
        String sql = "SELECT * FROM personagem_pericia";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }

    public List<PersonagemPericia> listarPorIdPersonagem(int idPersonagem) throws SQLException {
        List<PersonagemPericia> lista = new ArrayList<>();
        String sql = "SELECT * FROM personagem_pericia WHERE id_personagem = ?";
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
