package DAO;

import Model.PersonagemTalento;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonagemTalentoDAO {
    public void vincular(int idPersonagem, int idTalento)
            throws SQLException {
        try (Connection c = ConnectionFactory.getConnection(); PreparedStatement p = c.prepareStatement
                ("INSERT INTO personagem_talento(id_personagem,id_talento) VALUES(?,?)")) {
            p.setInt(1, idPersonagem);
            p.setInt(2, idTalento);
            p.executeUpdate();
        }
    }

    public List<PersonagemTalento> listarPorPersonagem(int idPersonagem)
            throws SQLException {
        List<PersonagemTalento> out = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection(); PreparedStatement p = c.prepareStatement
                ("SELECT id_personagem,id_talento FROM personagem_talento WHERE id_personagem=? ORDER BY id_talento")) {
            p.setInt(1, idPersonagem);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) out.add
                        (new PersonagemTalento(r.getInt("id_personagem"), r.getInt("id_talento")));
            }
        }
        return out;
    }

    public void desvincular(int idPersonagem, int idTalento)
            throws SQLException {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM personagem_talento WHERE id_personagem=? AND id_talento=?")) {
            p.setInt(1, idPersonagem);
            p.setInt(2, idTalento);
            p.executeUpdate();
        }
    }
}