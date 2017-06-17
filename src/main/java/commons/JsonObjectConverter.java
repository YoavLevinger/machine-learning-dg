package commons;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by Levinger on 27/12/2016.
 */
public class JsonObjectConverter {

    private static Gson gson = new GsonBuilder().serializeNulls().create();

    /**
     * Converting json to object
     * @param json - The json string
     * @param cls - The required object class
     * @param <T> - Generics...
     * @return - Required object
     */
    public static <T> T jsonToObject (String json, Class<T> cls){
        return gson.fromJson(json,cls);
    }



    public static <T> T jsonToObject(String json, Type typeOfT){
        return gson.fromJson(json, typeOfT);
    }

    /**
     * Object to json converter
     * @param obj - The Object to convert
     * @return - The generated json string
     */
    public static String objectToJson (Object obj){
        return gson.toJson(obj);
    }


}
