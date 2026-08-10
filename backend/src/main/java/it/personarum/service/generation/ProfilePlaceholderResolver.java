package it.personarum.service.generation;

import it.personarum.domain.profile.Profile;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ProfilePlaceholderResolver {

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String resolve(String content, Profile profile) {
        Map<String, String> values = new LinkedHashMap<>();

        values.put("firstName", value(profile.getFirstName()));
        values.put("lastName", value(profile.getLastName()));
        values.put("birthDate", profile.getBirthDate() == null
            ? ""
            : profile.getBirthDate().format(DATE_FORMAT));
        values.put("birthPlace", value(profile.getBirthPlace()));
        values.put("fiscalCode", value(profile.getFiscalCode()));
        values.put("email", value(profile.getEmail()));
        values.put("phone", value(profile.getPhone()));

        String result = content;

        for (Map.Entry<String, String> entry : values.entrySet()) {
            result = result.replace(
                "{" + entry.getKey() + "}",
                entry.getValue()
            );
        }

        return result;
    }

    private String value(String value) {
        return value == null ? "" : value;
    }
}
