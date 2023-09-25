package jp.vmware.tanzu.socialwordcloud.ai_rag.rag;

import jp.vmware.tanzu.socialwordcloud.ai_rag.record.VectorRecord;
import org.springframework.ai.autoconfigure.openai.OpenAiProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

	public enum PgDistanceType {

		EuclideanDistance("<->", "vector_l2_ops"), NegativeInnerProduct("<#>", "vector_ip_ops"),
		CosineDistance("<=>", "vector_cosine_ops");

		public final String operator;

		public final String index;

		PgDistanceType(String operator, String index) {
			this.operator = operator;
			this.index = index;
		}

	}

	public RetriveVectorTable(@Value("${openai.vector.table}") String vectorTable, JdbcTemplate jdbcTemplate,
			OpenAiProperties openAiProperties) {
		this.embeddingModels = openAiProperties.getEmbeddingModel();
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

		this.jdbcTemplate.execute("INSERT INTO " + vectorTable + "(id, message_id, vector) VALUES (nextval('"
				+ vectorTable + "_seq'), " + messageId + ", pgml.embed('" + embeddingModels + "', '" + context + "'))");

	}

	public List<VectorRecord> semanticSearchListId(String prompt, Integer limit) {
		VecordRecordMapper vecordRecordMapper = new VecordRecordMapper();
		return this.jdbcTemplate.query("SELECT DISTINCT ON (distance) id, message_id, pgml.embed('" + this.embeddingModels + "', '" + prompt
				+ "')::vector " + PgDistanceType.EuclideanDistance.operator + " vector AS distance FROM "
				+ this.vectorTable + " ORDER BY distance LIMIT " + limit, vecordRecordMapper);
	}

}
