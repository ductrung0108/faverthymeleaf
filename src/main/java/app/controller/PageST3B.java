package app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.config.JDBCConnection;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public class PageST3B implements Handler {

    public static final String URL = "/page3B.html";

    @Override
    public void handle(Context context) throws Exception {
        JDBCConnection connection = new JDBCConnection();

        // Get form parameters
        String commodityCode = context.formParam("commodityCode");
        String similarityMetric = context.formParam("similarityMetric");

        // Default values for initial load or when form is first loaded
        if (commodityCode == null || similarityMetric == null) {
            commodityCode = ""; // Default selection or empty string as per initial requirement
            similarityMetric = ""; // Default selection or empty string as per initial requirement
        }

        // Retrieve data based on form parameters
        List<String> cpcCodeList = connection.getCpcCodes();
        List<Map<String, Object>> similarGroups = null;

        // Process only if form is submitted with valid parameters
        if (!commodityCode.isEmpty() && !similarityMetric.isEmpty()) {
            similarGroups = connection.getSimilarGroups(commodityCode, similarityMetric);
        }

        Map<String, Object> model = new HashMap<>();
        model.put("cpcCodes", cpcCodeList);
        model.put("similarGroups", similarGroups);

        context.render("/templates/page3B.html", model);
    }
}
