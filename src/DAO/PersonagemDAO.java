package DAO;

import Model.Personagem;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonagemDAO {

    private static Integer nullableInt(ResultSet rs, String column) throws SQLException {
        Object value = rs.getObject(column);
        return value == null ? null : ((Number) value).intValue();
    }

    private static void setNullableInt(PreparedStatement stmt, int index, Integer value) throws SQLException {
        if (value == null) stmt.setNull(index, Types.INTEGER);
        else stmt.setInt(index, value);
    }

    public void inserir(Personagem p) {
        String sql = "INSERT INTO personagem (nome, nivel, id_classe, id_subclasse, id_raca, id_sub_raca, id_antecedente, id_player, id_atributos, id_mesa, hp_atual, hp_maximo, ca, deslocamento) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, p.getNome());
            stmt.setInt(2, p.getNivel());
            stmt.setInt(3, p.getIdClasse());
            setNullableInt(stmt, 4, p.getIdSubclasse());
            stmt.setInt(5, p.getIdRaca());
            setNullableInt(stmt, 6, p.getIdSubRaca());
            setNullableInt(stmt, 7, p.getIdAntecedente());
            stmt.setInt(8, p.getIdPlayer());
            stmt.setInt(9, p.getIdAtributos());
            setNullableInt(stmt, 10, p.getIdMesa());
            stmt.setInt(11, p.getHpAtual());
            stmt.setInt(12, p.getHpMaximo());
            stmt.setInt(13, p.getCa());
            stmt.setInt(14, p.getDeslocamento());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) p.setIdPersonagem(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir personagem: " + e.getMessage(), e);
        }
    }

    public List<Personagem> listar() {
        List<Personagem> lista = new ArrayList<>();
        String sql = "SELECT * FROM personagem";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Personagem p = new Personagem();
                p.setIdPersonagem(rs.getInt("id_personagem"));
                p.setNome(rs.getString("nome"));
                p.setNivel(rs.getInt("nivel"));
                p.setIdClasse(rs.getInt("id_classe"));
                p.setIdRaca(rs.getInt("id_raca"));
                p.setIdSubclasse(nullableInt(rs, "id_subclasse"));
                p.setIdSubRaca(nullableInt(rs, "id_sub_raca"));
                p.setIdAntecedente(nullableInt(rs, "id_antecedente"));
                p.setIdPlayer(rs.getInt("id_player"));
                p.setIdAtributos(rs.getInt("id_atributos"));
                p.setIdMesa(nullableInt(rs, "id_mesa"));
                p.setHpAtual(rs.getInt("hp_atual"));
                p.setHpMaximo(rs.getInt("hp_maximo"));
                p.setCa(rs.getInt("ca"));
                p.setDeslocamento(rs.getInt("deslocamento"));
                lista.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar personagens: " + e.getMessage(), e);
        }
        return lista;
    }

    public void atualizar(Personagem p) {
        String sql = "UPDATE personagem SET nome = ?, nivel = ?, hp_atual = ?, id_mesa = ? WHERE id_personagem = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getNome());
            stmt.setInt(2, p.getNivel());
            stmt.setInt(3, p.getHpAtual());
            if (p.getIdMesa() != null) {
                stmt.setInt(4, p.getIdMesa());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setInt(5, p.getIdPersonagem());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar personagem: " + e.getMessage(), e);
        }
    }

    public void atualizarCompleto(Personagem p) {
        String sql = "UPDATE personagem SET nome=?, nivel=?, id_classe=?, id_subclasse=?, id_raca=?, id_sub_raca=?, id_antecedente=?, id_player=?, id_atributos=?, id_mesa=?, hp_atual=?, hp_maximo=?, ca=?, deslocamento=? WHERE id_personagem=?";
        try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getNome());
            stmt.setInt(2, p.getNivel());
            stmt.setInt(3, p.getIdClasse());
            setNullableInt(stmt, 4, p.getIdSubclasse());
            stmt.setInt(5, p.getIdRaca());
            setNullableInt(stmt, 6, p.getIdSubRaca());
            setNullableInt(stmt, 7, p.getIdAntecedente());
            stmt.setInt(8, p.getIdPlayer());
            stmt.setInt(9, p.getIdAtributos());
            setNullableInt(stmt, 10, p.getIdMesa());
            stmt.setInt(11, p.getHpAtual());
            stmt.setInt(12, p.getHpMaximo());
            stmt.setInt(13, p.getCa());
            stmt.setInt(14, p.getDeslocamento());
            stmt.setInt(15, p.getIdPersonagem());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar personagem: " + e.getMessage(), e);
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM personagem WHERE id_personagem = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar personagem: " + e.getMessage(), e);
        }
    }

    // --- PV durante a mesa ---
    // delta < 0 é dano e delta > 0 é cura. O HP fica sempre entre 0 e hp_maximo. O trigger trg_personagem_au_log registra a mudança.

    // O jogador só altera o PV do próprio personagem. false = não existe ou não é dele.
    public boolean ajustarHp(int idPersonagem, int idPlayer, int delta) {
        String sql = "UPDATE personagem SET hp_atual = GREATEST(0, LEAST(hp_maximo, hp_atual + ?)) WHERE id_personagem = ? AND id_player = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, delta);
            stmt.setInt(2, idPersonagem);
            stmt.setInt(3, idPlayer);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ajustar PV do personagem: " + e.getMessage(), e);
        }
    }

    // O mestre altera o PV dos personagens que estão na mesa dele. false = personagem fora das mesas desse mestre.
    public boolean ajustarHpPeloMestre(int idPersonagem, int idMestre, int delta) {
        String sql = "UPDATE personagem p JOIN mesa m ON m.id_mesa = p.id_mesa "
                + "SET p.hp_atual = GREATEST(0, LEAST(p.hp_maximo, p.hp_atual + ?)) "
                + "WHERE p.id_personagem = ? AND m.id_mestre = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, delta);
            stmt.setInt(2, idPersonagem);
            stmt.setInt(3, idMestre);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ajustar PV pelo mestre: " + e.getMessage(), e);
        }
    }

    // Descanso longo: PV cheios. Só o dono do personagem.
    public boolean descansoLongo(int idPersonagem, int idPlayer) {
        String sql = "UPDATE personagem SET hp_atual = hp_maximo WHERE id_personagem = ? AND id_player = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPersonagem);
            stmt.setInt(2, idPlayer);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro no descanso longo: " + e.getMessage(), e);
        }
    }
}
