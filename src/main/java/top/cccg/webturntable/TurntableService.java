package top.cccg.webturntable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TurntableService {
    private static final String DATA_FILE = "turntable_entries.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<TurntableEntry> entries = new ArrayList<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @PostConstruct
    public void init() {
        loadEntries();
    }

    public synchronized List<TurntableEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    public synchronized TurntableEntry addEntry(String text) {
        TurntableEntry entry = new TurntableEntry(idGen.getAndIncrement(), text);
        entries.add(entry);
        saveEntries();
        return entry;
    }

    public synchronized boolean deleteEntry(Long id) {
        boolean removed = entries.removeIf(e -> e.getId().equals(id));
        if (removed) saveEntries();
        return removed;
    }

    private void loadEntries() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try {
                List<TurntableEntry> loaded = objectMapper.readValue(file, new TypeReference<List<TurntableEntry>>(){});
                entries.clear();
                entries.addAll(loaded);
                long maxId = entries.stream().mapToLong(TurntableEntry::getId).max().orElse(0L);
                idGen.set(maxId + 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveEntries() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(DATA_FILE), entries);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
