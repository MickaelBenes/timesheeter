package fr.mikaelbenes.timesheeter.data.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

@Converter(autoApply = true)
@SuppressWarnings("unused")
public class LocalDateTimeAttributeConverter implements AttributeConverter<LocalDateTime, Date> {

	@Override
	public Date convertToDatabaseColumn(LocalDateTime locDate) {
		if (Objects.isNull(locDate)) {
			return null;
		}

		Instant instant = locDate.toInstant(OffsetDateTime.now().getOffset());
		return Date.from(instant);
	}

	@Override
	public LocalDateTime convertToEntityAttribute(Date sqlDate) {
		if (Objects.isNull(sqlDate)) {
			return null;
		}

		return LocalDateTime.ofInstant(sqlDate.toInstant(), ZoneId.of("Europe/Paris"));
	}

}
