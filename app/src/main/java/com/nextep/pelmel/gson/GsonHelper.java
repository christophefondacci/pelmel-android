package com.nextep.pelmel.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.nextep.json.model.IJsonLightPlace;
import com.nextep.json.model.IJsonLightUser;
import com.nextep.json.model.impl.JsonLightPlace;
import com.nextep.json.model.impl.JsonLightUser;
import com.nextep.json.model.impl.JsonPlace;
import com.nextep.json.model.impl.JsonPlaceOverview;
import com.nextep.json.model.impl.JsonUser;

import io.gsonfire.GsonFireBuilder;
import io.gsonfire.TypeSelector;

/**
 * Created by cfondacci on 21/07/15.
 */
public final class GsonHelper {

    private static Gson gson;
    private GsonHelper() {}

    public static Gson getGson() {
        if(gson == null) {
            gson = new GsonFireBuilder().registerTypeSelector(IJsonLightUser.class, new TypeSelector<IJsonLightUser>() {
                @Override
                public Class<? extends IJsonLightUser> getClassForElement(JsonElement readElement) {

                    boolean isLight = readElement.getAsJsonObject().has("thumb");
                    if(isLight) {
                        return JsonLightUser.class;
                    } else {
                        return JsonUser.class;
                    }
                }
            }).registerTypeSelector(IJsonLightPlace.class, new TypeSelector<IJsonLightPlace>() {
                @Override
                public Class<? extends IJsonLightPlace> getClassForElement(JsonElement readElement) {
                    if (readElement.isJsonNull()) {
                        return JsonLightPlace.class;
                    } else {
                        boolean hasCommentsCount = readElement.getAsJsonObject().has("commentsCount");
                        boolean hasRawDistance = readElement.getAsJsonObject().has("rawDistance");
                        if (hasCommentsCount) {
                            return JsonPlaceOverview.class;
                        } else if (hasRawDistance) {
                            return JsonPlace.class;
                        } else {
                            return JsonLightPlace.class;
                        }
                    }
                }
            }).createGson();
        }
        return gson;
    }

}
