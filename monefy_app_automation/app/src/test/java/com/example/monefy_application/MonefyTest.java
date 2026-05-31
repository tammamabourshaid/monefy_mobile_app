package com.example.monefy_application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.MobileCapabilityType;

public class MonefyTest {
    AndroidDriver<AndroidElement> driver;
    final Logger logger = LogManager.getLogger();

    @BeforeTest
    public void setup() throws MalformedURLException {
        DesiredCapabilities dc = new DesiredCapabilities();
        dc.setCapability(MobileCapabilityType.DEVICE_NAME,"emulator-5554");
        dc.setCapability("platformName","android");
        dc.setCapability("appPackage","com.monefy.app.lite");
        dc.setCapability("appActivity","com.monefy.activities.main.MainActivity_");
        driver = new AndroidDriver<AndroidElement>(new URL("http://127.0.0.1:4723/wd/hub"),dc);
    }

    @Test(testName = "Add Income", priority = 1)
    public void addIncome() {
        logger.info("Run Add Income Test Cases!");
        AddIncomeAndAssert.addIncome(driver);
    }

    @Test(testName = "Add Bills", priority = 2)
    public void addBills() {
        logger.info("Run Add Bills Expense Test Cases!");
        AddExpenseBillsAndAssert.addBillsToExpense(driver);
    }

    @Test(testName = "Add Foods", priority = 3)
    public void addFoods() {
        logger.info("Run Add Foods Expense Test Cases!");
        AddExpenseFoodAndAssert.addFoodsToExpense(driver);
    }

    @Test(testName = "Percentage", priority = 4)
    public void percentage() {
        logger.info("Run Percentage Test Cases!");
        PercentageOfFoodExpense.ExpensePercentage(driver);
    }

    @Test(testName = "Delete Food Expense", priority = 5)
    public void expenseManagement() {
        logger.info("Run Delete Food Expense Test Cases!");
        DeleteFoodExpenseAndAssert.deleteExpense(driver);
    }

    @AfterTest
    public void tearDown() {
        logger.info("End Test Case!");
        if (driver != null) {
            driver.quit();
        }

    }
}
