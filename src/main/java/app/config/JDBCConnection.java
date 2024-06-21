package app.config;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.dto.Task2B;
import app.entities.Commodity;
import app.entities.Country;
import app.entities.Date;
import app.entities.FoodLossEvent;

import java.util.Map;

/**
 * Class for Managing the JDBC Connection to a SQLLite Database.
 * Allows SQL queries to be used with the SQLLite Databse in Java.
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @author Halil Ali, 2024. email: halil.ali@rmit.edu.au
 */

public class JDBCConnection {

    // Name of database file (contained in database folder)
    public static final String DATABASE = "jdbc:sqlite:database/foodloss.db";

    /**
     * This creates a JDBC Object so we can keep talking to the database
     */
    public JDBCConnection() {
        System.out.println("Created JDBC Connection Object");
    }

    /**
     * Get all of the Countries in the database.
     * 
     * @return
     *         Returns an ArrayList of Country objects
     */
    public ArrayList<Country> getAllCountries() {
        // Create the ArrayList of Country objects to return
        ArrayList<Country> countries = new ArrayList<Country>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT * FROM Country";

            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Lookup the columns we need
                String m49Code = results.getString("m49code");
                String name = results.getString("countryName");

                // Create a Country Object
                Country country = new Country(m49Code, name);

                // Add the Country object to the array
                countries.add(country);
            }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the countries
        return countries;
    }

    public ArrayList<Commodity> getAllCommodity() {
        // Create the ArrayList of Country objects to return
        ArrayList<Commodity> commodities = new ArrayList<Commodity>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT *  FROM commodity WHERE group_code='' ORDER BY Descriptor";

            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Lookup the columns we need
                String cpcCode = results.getString("cpc_code");
                String descriptor = results.getString("descriptor");
                String groupCode = results.getString("group_code");

                // Create a Country Object
                Commodity commodity = new Commodity(cpcCode, descriptor, groupCode);

                // Add the Country object to the array
                commodities.add(commodity);
            }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the countries
        return commodities;
    }

    public ArrayList<Date> getAllYears() {
        // Create the ArrayList of Country objects to return
        ArrayList<Date> years = new ArrayList<Date>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT * FROM Date";

            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Lookup the columns we need
                Integer yearString = results.getInt("year");

                // Create a Country Object
                Date year = new Date(yearString);

                // Add the Country object to the array
                years.add(year);
            }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the countries
        return years;
    }
    // TODO: Add your required methods here

    public ArrayList<Task2B> getAllFoodLossEvent(String fromYear, String toYear,
            List<String> foodGroupList, String orderBy, boolean ac, boolean fs, boolean ca) {
        // Create the ArrayList of Country objects to return
        ArrayList<Task2B> foodLossEvents = new ArrayList<Task2B>();

        if (foodGroupList == null)
            return foodLossEvents;

        String filter = "", f2 = "",filter1="";
        if (ac) {
            filter += ",activity";
            filter1 += ",activity as activity1";
            f2 += " and s.activity=e.activity1";
        }
        if (fs) {
            filter += ",food_supply_stage";
            filter1 += ",food_supply_stage as food_supply_stage1";
            f2 += " and s.food_supply_stage=e.food_supply_stage1";
        }
        if (ca) {
            filter += ",cause_off_loss";
            filter1 += ",cause_off_loss as cause_off_loss1";
            f2 += " and s.cause_off_loss=e.cause_off_loss1";
        }

        String fGStr = "";
        fGStr = "foodgroup= '" + foodGroupList.get(0) + "'";
        for (int i = 1; i < foodGroupList.size(); i++) {
            fGStr += " or foodgroup= '" + foodGroupList.get(i) + "'";
        }

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            // TEST QUERY
            String query = "with com as (";
            query += " select c1.cpc_code as cpc_code,c2.descriptor as foodgroup from commodity c1 join commodity c2 on c1.group_code=c2.cpc_code)";
            query += " select (endd-start)/start*100 as diff,* from";
            query += " (SELECT foodgroup,avg(loss_percentage) as start " + filter;
            query += " FROM (FoodLossEvent f join com c on f.cpc_code=c.cpc_code)";
            query += " WHERE (year= '" + fromYear + "') and (" + fGStr + ")";
            query += " GROUP BY foodgroup " + filter + ") s";
            // query += (missing ? " full outer" : " inner") + " join";
            query += " full outer join";
            query += " (SELECT foodgroup as foodgroup1,avg(loss_percentage) as endd " + filter1;
            query += " FROM (FoodLossEvent f join com c on f.cpc_code=c.cpc_code)";
            query += " WHERE (year= '" + toYear + "') and (" + fGStr + ")";
            query += " GROUP BY foodgroup " + filter + ") e";
            query += " on s.foodgroup=e.foodgroup1" + f2;
            query += " order by '" + orderBy + "'";

            // Get Result
            System.out.println(query);
            ResultSet results = statement.executeQuery(query);
            // Process all of the results
            while (results.next()) {
                String commodity = results.getString("foodgroup") != null ? results.getString("foodgroup")
                        : results.getString("foodgroup1");
                double start = results.getDouble("start");
                double end = results.getDouble("endd");
                Double lossPercentage = results.getDouble("diff");
                String activity = ac ? (results.getString("activity") != null ? results.getString("activity")
                        : results.getString("activity1")) : null;
                String foodSupplyStage = fs ? results.getString("food_supply_stage") : null;
                String causeOffLoss = ca ? results.getString("cause_off_loss") : null;
                // Create a Task2B Object
                Task2B foodLossEvent = new Task2B(commodity, start, end, lossPercentage, activity, foodSupplyStage,
                        causeOffLoss);

                // Add the Task2B object to the array
                foodLossEvents.add(foodLossEvent);
            }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the countries
        return foodLossEvents;
    }

    public List<Map<String, Object>> getSimilarGroups(String commodityCode, String similarityMetric) {
        List<Map<String, Object>> results = new ArrayList<>();

        // Database connection setup
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            // Prepare the SQL query
            String sql = """
                        WITH CommodityGroup AS (
                            SELECT DISTINCT c.group_code
                            FROM Commodity c
                            WHERE c.cpc_code = ?
                        ),
                        GroupMetrics AS (
                            SELECT
                                c.group_code,
                                AVG(f.loss_percentage) AS avg_loss,
                                MAX(f.loss_percentage) AS max_loss,
                                MIN(f.loss_percentage) AS min_loss
                            FROM FoodLossEvent f
                            JOIN Commodity c ON f.cpc_code = c.cpc_code
                            GROUP BY c.group_code
                        ),
                        SelectedGroupMetrics AS (
                            SELECT
                                g.group_code,
                                g.avg_loss,
                                g.max_loss,
                                g.min_loss
                            FROM GroupMetrics g
                            JOIN CommodityGroup cg ON g.group_code = cg.group_code
                        ),
                        SimilarGroups AS (
                            SELECT
                                gm.group_code,
                                gm.avg_loss,
                                gm.max_loss,
                                gm.min_loss,
                                ABS(gm.avg_loss - sg.avg_loss) AS avg_diff,
                                ABS(gm.max_loss - sg.max_loss) AS max_diff,
                                ABS(gm.min_loss - sg.min_loss) AS min_diff
                            FROM GroupMetrics gm
                            CROSS JOIN SelectedGroupMetrics sg
                        )
                        SELECT
                            s.group_code,
                            (SELECT Descriptor FROM Commodity WHERE group_code = s.group_code LIMIT 1) AS group_name,
                            s.avg_loss,
                            s.max_loss,
                            s.min_loss,
                            CASE ? -- Change to the user-selected similarity metric ('avg', 'max', or 'min')
                                WHEN 'avg' THEN s.avg_diff
                                WHEN 'max' THEN s.max_diff
                                WHEN 'min' THEN s.min_diff
                            END AS similarity_score
                        FROM SimilarGroups s
                        ORDER BY similarity_score
                    """;

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, commodityCode);
            preparedStatement.setString(2, similarityMetric);

            // Execute the query
            resultSet = preparedStatement.executeQuery();

            // Process ResultSet and populate results
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("group_code", resultSet.getString("group_code"));
                row.put("group_name", resultSet.getString("group_name"));
                row.put("avg_loss", resultSet.getDouble("avg_loss"));
                row.put("max_loss", resultSet.getDouble("max_loss"));
                row.put("min_loss", resultSet.getDouble("min_loss"));
                row.put("similarity_score", resultSet.getDouble("similarity_score"));
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (resultSet != null)
                    resultSet.close();
                if (preparedStatement != null)
                    preparedStatement.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    public List<String> getCpcCodes() {
        List<String> cpcCodes = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            // Prepare the SQL query
            String sql = """
                        WITH CommodityGroup AS (
                            SELECT DISTINCT c.group_code
                            FROM Commodity c
                        ), GroupMetrics AS (
                            SELECT
                                c.group_code,
                                AVG(f.loss_percentage) AS avg_loss,
                                MAX(f.loss_percentage) AS max_loss,
                                MIN(f.loss_percentage) AS min_loss
                            FROM FoodLossEvent f
                            JOIN Commodity c ON f.cpc_code = c.cpc_code
                            WHERE c.group_code IN (SELECT group_code FROM CommodityGroup)
                            GROUP BY c.group_code
                        ), SelectedGroupMetrics AS (
                            SELECT
                                g.group_code,
                                g.avg_loss,
                                g.max_loss,
                                g.min_loss
                            FROM GroupMetrics g
                            JOIN CommodityGroup cg ON g.group_code = cg.group_code
                        ), SimilarGroups AS (
                            SELECT
                                gm.group_code,
                                gm.avg_loss,
                                gm.max_loss,
                                gm.min_loss,
                                ABS(gm.avg_loss - sg.avg_loss) AS avg_diff,
                                ABS(gm.max_loss - sg.max_loss) AS max_diff,
                                ABS(gm.min_loss - sg.min_loss) AS min_diff
                            FROM GroupMetrics gm
                            CROSS JOIN SelectedGroupMetrics sg
                        )
                        SELECT DISTINCT c.cpc_code
                        FROM Commodity c
                        JOIN SimilarGroups s ON c.group_code = s.group_code
                        ORDER BY c.cpc_code;    
                    """;

            preparedStatement = connection.prepareStatement(sql);

            // Execute the query
            resultSet = preparedStatement.executeQuery();

            // Process ResultSet and populate cpcCodes list
            while (resultSet.next()) {
                String cpcCode = resultSet.getString("cpc_code");
                cpcCodes.add(cpcCode);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception (log, throw, etc.)
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle exception (log, throw, etc.)
            }
        }

        return cpcCodes;
    }
}
