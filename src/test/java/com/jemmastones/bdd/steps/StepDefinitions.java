package com.jemmastones.bdd.steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.*;
import java.sql.*;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class StepDefinitions {

    private Connection mySqlConn = null;
    private PreparedStatement stmt = null;
    private ResultSet rs = null;

    List<String> srcResults = new ArrayList<String>();
    List<String> rvResults = new ArrayList<String>();
    List<String> vwResults  = new ArrayList<String>();

    // Set Up Database Connection
    @Given("^User is successfully connected to the database$")
    public void getDbConnection () throws Throwable {
        String url = "jdbc:mysql://cucumber-demo-db.cklackunfivv.ap-southeast-2.rds.amazonaws.com:3306/RV";
        String user = "master";
        String password = "Servian2017";


        mySqlConn = DriverManager.getConnection(url, user, password);
        if (mySqlConn == null) {
            throw new Exception("Can't connect to selected database");
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    if (!mySqlConn.isClosed()) {
                        mySqlConn.close();
                    }
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });
    }

    // Source Results with No Predicate
    @When("^I get \"(.*?)\" from the \"(.*?)\" source table$")
    public void getSourceResultsNoPredicate(String sel, String tbl) throws Throwable {
        try {
            String sqlTemplate = "select " + sel + " from sakila." + tbl;

            stmt = mySqlConn.prepareStatement(sqlTemplate);
            rs = stmt.executeQuery();
            while(rs.next()) {
                String attribute = rs.getString(1);
                srcResults.add(attribute);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    // Source Results with Predicate
    @When("^I get \"(.*?)\" from the \"(.*?)\" source table for records with \"(.*?)\" = (\\d+)$")
    public void getSourceResultsWithPredicate(String sel, String tbl, String prd, int val) throws Throwable {
        try {
            String sqlTemplate = "select " + sel + " from sakila." + tbl +
                    " where " + prd + " = " + val;

            stmt = mySqlConn.prepareStatement(sqlTemplate);
            rs = stmt.executeQuery();
            while(rs.next()) {
                String attribute = rs.getString(1);
                srcResults.add(attribute);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

    }


    // Raw Vault Results with No Predicate
    @When("^I get \"(.*?)\" from the \"(.*?)\" raw vault table$")
    public void getRawResultsNoPredicate(String sel, String tbl) throws Throwable {
        try {
            String sqlTemplate = "select " + sel + " from RV." + tbl;

            stmt = mySqlConn.prepareStatement(sqlTemplate);
            rs = stmt.executeQuery();
            while(rs.next()) {
                String attribute = rs.getString(1);
                rvResults.add(attribute);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    // Raw Vault Results with Predicate
    @When("^I get \"(.*?)\" from the \"(.*?)\" raw vault table for records with \"(.*?)\" = (\\d+)$")
    public void getRawResultsWithPredicate(String sel, String tbl, String prd, int val) throws Throwable {
        try {
            String sqlTemplate = "select " + sel + " from RV." + tbl +
                    " where " + prd + " = " + val;

            stmt = mySqlConn.prepareStatement(sqlTemplate);
            rs = stmt.executeQuery();
            while(rs.next()) {
                String attribute = rs.getString(1);
                rvResults.add(attribute);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    // Consumption View Results with no Predicate
    @When("^I get \"(.*?)\" from the \"(.*?)\" consumption view$")
    public void getConsumptionResultsNoPredicate(String sel, String tbl) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        try {
            String sqlTemplate = "select " + sel + " from PRES." + tbl;

            stmt = mySqlConn.prepareStatement(sqlTemplate);
            rs = stmt.executeQuery();
            while(rs.next()) {
                String attribute = rs.getString(1);
                vwResults.add(attribute);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // Consumption View Results with Predicate
    @When("^I get \"(.*?)\" from the \"(.*?)\" consumption view for records with \"(.*?)\" = \"(.*?)\"$")
    public void getConsumptionResultsWithPredicate(String sel, String tbl, String prd, String val) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        try {
            String sqlTemplate = "select " + sel + " from PRES." + tbl +
                    " where " + prd + " = " + val;

            stmt = mySqlConn.prepareStatement(sqlTemplate);
            rs = stmt.executeQuery();
            while(rs.next()) {
                String attribute = rs.getString(1);
                vwResults.add(attribute);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // Check for existing columns in Vault Hub Tables
    @When("^I list the columns in the \"(.*?)\" raw vault table$")
    public void getRawVaultColumns(String tbl) throws Throwable {
        try {
            String sqlTemplate = "show fields from "+" RV." + tbl;

            stmt = mySqlConn.prepareStatement(sqlTemplate);
            rs = stmt.executeQuery();
            while(rs.next()) {
                String attribute = rs.getString(1);
                rvResults.add(attribute);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    // Check for duplicates in column
    @When("^I count the number of duplicate records in the \"(.*?)\" column of the \"(.*?)\" table$")
    public void getDuplicates(String col, String tbl) throws Throwable {
        try {
            String sqlTemplate = "select " + col + ", count(*) from RV." + tbl + " group by " + col + " having count(*) > 1";

            stmt = mySqlConn.prepareStatement(sqlTemplate);
            rs = stmt.executeQuery();
            while(rs.next()) {
                String attribute = rs.getString(1);
                rvResults.add(attribute);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }



    // Check that the first result set does *not* overlap with the second
    @Then("^records in the first set should not be present in the second set$")
    public void checkNotSubset() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        try {
            if (srcResults.size()>0 && vwResults.size()>0) {
                assertTrue(Collections.disjoint(srcResults,vwResults));

            }
            else if (srcResults.size()>0 && rvResults.size()>0) {
                assertTrue(Collections.disjoint(srcResults,rvResults));
            }
            else {
                assertTrue(false);
            }

        }
        catch(Exception e) {
            System.out.println(e);
        }

    }

    // Check that the size of both results sets is equal
    @Then("^number of records returned should be identical$")
    public void checkEqualSize() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        try {
            if (srcResults.size()>0 && rvResults.size()>0) {
                assertTrue(srcResults.size() == rvResults.size());;
            }
            else if (srcResults.size()>0 && vwResults.size()>0) {
                assertTrue(rvResults.size() == vwResults.size());;
            }
            else {
                assertTrue(false);
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }

    }

    @Then("^I should find \"(.*?)\", \"(.*?)\" and \"(.*?)\" columns$")
    public void i_should_find_and_columns(String arg1, String arg2, String arg3) throws Throwable {

        try {
            assertTrue(rvResults.contains(arg1) && rvResults.contains(arg2) && rvResults.contains(arg3));
        }
        catch(Exception e) {
            System.out.println(e);
        }

    }

    @Then("^I should get exactly (\\d+) results$")
    public void checkNumberOfResults(int arg1) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        try {
            assertTrue(rvResults.size() == arg1);
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }





}