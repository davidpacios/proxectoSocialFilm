package gal.usc.etse.grei.es.project.service;

import java.util.List;
import java.util.Map;

import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PatchUtils {
    private final ObjectMapper mapper;

    @Autowired
    public PatchUtils(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @SuppressWarnings("unchecked")
    public <T> T patch(T data, List<Map<String, Object>> updates) throws JsonPatchException, JsonPatchException {
        // Translate the set of operations to a JsonPatch instance
        JsonPatch operations = mapper.convertValue(updates, JsonPatch.class);
        // Translate domain object to plain JSON node
        JsonNode json = mapper.convertValue(data, JsonNode.class);
        // Apply operations to the JSON node
        JsonNode updatedJson = operations.apply(json);
        // Volvemos a transformar o JSON nunha instancia de usuario empregando Jackson
        return (T) mapper.convertValue(updatedJson, data.getClass());
    }
}
