package dogapi;

import java.util.*;

public class CachingBreedFetcher implements BreedFetcher {
    private final BreedFetcher fetcher;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = Objects.requireNonNull(fetcher);
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedFetcher.BreedNotFoundException {
        // Return cached on hit
        if (cache.containsKey(breed)) {
            return cache.get(breed);
        }

        // Always count an underlying call attempt (success or failure)
        callsMade++;
        try {
            List<String> result = fetcher.getSubBreeds(breed);
            // Cache only successful results
            cache.put(breed, result);
            return result;
        } catch (BreedFetcher.BreedNotFoundException e) {
            // Do NOT cache failures — just rethrow
            throw e;
        }
    }

    public int getCallsMade() {
        return callsMade;
    }
}
