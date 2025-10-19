package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public List<String> getSubBreeds(String breed) throws BreedFetcher.BreedNotFoundException {
        String url = "https://dog.ceo/api/breed/" + breed.toLowerCase() + "/list";
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                throw new BreedFetcher.BreedNotFoundException("Empty response for breed: " + breed);
            }

            String body = response.body().string();
            JSONObject json = new JSONObject(body);

            // Dog CEO returns { "status": "success" | "error", "message": ... }
            if (!"success".equalsIgnoreCase(json.optString("status"))) {
                throw new BreedFetcher.BreedNotFoundException(
                        json.optString("message", "Breed not found: " + breed)
                );
            }

            JSONArray messageArray = json.getJSONArray("message");
            List<String> subBreeds = new ArrayList<>(messageArray.length());
            for (int i = 0; i < messageArray.length(); i++) {
                subBreeds.add(messageArray.getString(i));
            }
            return subBreeds;

        } catch (IOException | org.json.JSONException e) {
            // Per assignment: treat any failure as BreedNotFoundException
            throw new BreedFetcher.BreedNotFoundException("Error fetching breed data: " + e.getMessage());
        }
    }
}
