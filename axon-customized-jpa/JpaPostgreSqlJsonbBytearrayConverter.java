import java.nio.charset.StandardCharsets;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = false)
public class JpaPostgreSqlJsonbBytearrayConverter implements AttributeConverter<byte[], Object> {

	@Override
	public String convertToDatabaseColumn(byte[] bytes) {
		return (bytes != null) ? new String(bytes, StandardCharsets.UTF_8) : null;
	}

	@Override
	public byte[] convertToEntityAttribute(Object json) {
		return (json != null) ? json.toString().getBytes(StandardCharsets.UTF_8) : null;
	}

	@Override
	public String toString() {
		return "JpaPostgreSqlJsonbBytearrayConverter []";
	}
}
