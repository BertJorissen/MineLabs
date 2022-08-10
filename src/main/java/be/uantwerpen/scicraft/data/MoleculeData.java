package be.uantwerpen.scicraft.data;

import be.uantwerpen.scicraft.Scicraft;
import be.uantwerpen.scicraft.crafting.molecules.Atom;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class MoleculeData {
    private static final Logger LOGGER = LogManager.getLogger();

    private final String name;
    private final Map<String, Integer> data;

    public MoleculeData(String name, Map<String, Integer> data) {
        this.name = name;
        this.data = data;
    }

    public void serialize(JsonObject json) {
        JsonArray entries = new JsonArray();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            JsonObject jsonEntry = new JsonObject();
            jsonEntry.add("type", new JsonPrimitive(new Identifier("minecraft", "item").toString()));
            Atom atom = Atom.getBySymbol(entry.getKey());
            if (atom != null) {
                jsonEntry.add("name", new JsonPrimitive(atom.getItemId().toString()));
            } else {
                throw new IllegalStateException("Atom not found for symbol " + entry.getKey());
            }
            jsonEntry.add("weight", new JsonPrimitive(entry.getValue()));
            entries.add(jsonEntry);
        }

        JsonObject pool = new JsonObject();
        pool.add("entries", entries);
        pool.add("rolls", new JsonPrimitive(1));
        JsonArray pools = new JsonArray();
        pools.add(pool);
        json.add("pools", pools);
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        this.serialize(jsonObject);
        return jsonObject;
    }

    public Identifier getId() {
        return new Identifier(Scicraft.MOD_ID, name);
    }
}
