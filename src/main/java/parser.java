import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class parser {

    private static Path testPath = Paths.get("./src/main/resources/response.json");
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static List<DocumentInfo> docList = new ArrayList<>();


    private static List<Object> getData() {

        String content = "";
        try {
            content = new String(Files.readAllBytes(testPath));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Something went wrong");
        }
        JSONObject JsonObject = new JSONObject(content);
        return JsonObject.getJSONObject("payload").getJSONArray("items").toList();
    }

    public static Map getStatuses() {
        ArrayList<String> statuses = new ArrayList();
        getData().forEach(docum ->
                statuses.add((objectMapper.convertValue(docum, Map.class).get("status").toString())));
        return statuses.stream().collect(Collectors.groupingBy(s -> s, Collectors.counting()));
    }


    public static String getDataByFileName(String filename) {

        String result = null;
        getData().forEach(docum ->
                docList.add(objectMapper.convertValue(docum, DocumentInfo.class)));

        try {
            result = objectMapper.writeValueAsString(docList.stream().filter(documentInfo ->
                    documentInfo.file_name.equals(filename)).collect(Collectors.toList()));
        } catch (JsonProcessingException e) {
            System.out.println("Something went wrong");
            e.printStackTrace();
        }
        return result;

    }

    public static Table getDataByStatus(String status) {


        Table<Integer, String, String> statusTable
                = HashBasedTable.create();

        getData().forEach(docum ->
                docList.add(objectMapper.convertValue(docum, DocumentInfo.class)));

        List toTable = docList.stream().filter(documentInfo ->
                documentInfo.status.equals(status)).collect(Collectors.toList());


        for (int i = 0; i < toTable.size(); i++) {


            statusTable.put(i, "collection_id", ((DocumentInfo) toTable.get(i)).collection_id);
            statusTable.put(i, "created_date", ((DocumentInfo) toTable.get(i)).created_date);
            statusTable.put(i, "collection_id", ((DocumentInfo) toTable.get(i)).document_id);
            statusTable.put(i, "created_date", ((DocumentInfo) toTable.get(i)).file_name);
            statusTable.put(i, "collection_id", ((DocumentInfo) toTable.get(i)).revision_number);

        }


        return statusTable;
    }


    public static void main(String[] args)   {

        getStatuses();
        getDataByStatus("MIGRATED");
        getDataByFileName("64.pdf");
        System.out.println("igogo");

    }
}



