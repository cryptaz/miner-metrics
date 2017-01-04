package org.cryptaz.minermetrics;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class TemplateConfigurator {

    public TemplateConfigurator() {
    }

    public String template(int cardCount) throws IOException {
        InputStream baseInputStream = getClass().getClassLoader().getResourceAsStream("grafana_base.json");
        InputStream rowInputStream = getClass().getClassLoader().getResourceAsStream("grafana_card_row.json");
        String cardRowTemplateJson = IOUtils.toString(rowInputStream, "UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(baseInputStream);
        ObjectNode result = jsonNode.deepCopy();
        ArrayNode rows = jsonNode.get("rows").deepCopy();
        for (int i = 0; i < cardCount; i++) {
            String json = cardRowTemplateJson.replaceAll("\\*", String.valueOf(i));
            JsonNode templateJsonNode = objectMapper.readTree(json);
            ArrayNode templateNodes = templateJsonNode.get("rows").deepCopy();
            rows.add(templateNodes.get(0));
            rows.add(templateNodes.get(1));
        }

        result.remove("rows");
        result.set("rows", rows);

        return objectMapper.writeValueAsString(result);
    }
}
