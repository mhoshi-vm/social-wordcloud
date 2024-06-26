package jp.vmware.tanzu.socialwordcloud.ai_rag.rag;

import jp.vmware.tanzu.socialwordcloud.ai_rag.record.VectorRecord;
import org.springframework.ai.autoconfigure.openai.OpenAiEmbeddingProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class RetriveVectorTable {

	public String embeddingModels;

	public String vectorTable;

	public JdbcTemplate jdbcTemplate;

	public RetriveVectorTable(@Value("${openai.vector.table}") String vectorTable, JdbcTemplate jdbcTemplate,
			OpenAiEmbeddingProperties openAiProperties) {
		this.embeddingModels = openAiProperties.getOptions().getModel();
		this.jdbcTemplate = jdbcTemplate;
		this.vectorTable = vectorTable;
	}

	public static class VecordRecordMapper implements RowMapper<VectorRecord> {

		@Override
		public VectorRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new VectorRecord(rs.getLong("id"), rs.getString("message_id"), rs.getFloat("distance"));
		}

	}

	public void insertIntoDb(String messageId, String context) {

		this.jdbcTemplate.update("INSERT INTO " + this.vectorTable + "(id, message_id, vector) VALUES (nextval('"
				+ this.vectorTable + "_seq'), ?, pgml.embed(?, ?))", messageId, this.embeddingModels, context);

	}

	public List<VectorRecord> semanticSearchListId(String prompt, Integer limit) {
		VecordRecordMapper vecordRecordMapper = new VecordRecordMapper();
		return this.jdbcTemplate.query(
				"SELECT DISTINCT ON (distance) id, message_id, pgml.embed(?, ?)::vector <-> vector AS distance FROM "
						+ this.vectorTable + " ORDER BY distance LIMIT ?",
				vecordRecordMapper, this.embeddingModels, prompt, limit);
	}

}
