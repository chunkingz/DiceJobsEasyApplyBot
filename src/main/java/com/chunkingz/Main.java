package com.chunkingz;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

public class Main {
    private static final String baseUrl = JsonConfig.getBaseURL();
    private static final int numberOfJobsPerPage = JsonConfig.getNumberOfJobsPerPage();
    private static final String[] keywords = JsonConfig.getKeywords();
    private static final WebDriver driver = new ChromeDriver();

    // "Software Engineer" OR "Full Stack Engineer" OR "fullstack developer" OR "full stack developer" OR "javascript developer" OR "nodejs developer" OR "Java Developer" OR "Java Engineer"

    public static void main(String[] args) {
        header();
        initializer();
    }

    private static void header() {
        System.out.println("=====================================");
        System.out.println("\t\tWelcome to Dice Bot!");
        System.out.println("=====================================");
    }

    private static void initializer() {
        try {
            driver.manage().window().maximize();
            driver.get(baseUrl);
            login();
            searchAndFilter();
            fillAndApply();
            pause(5000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            driver.quit();
        }
    }

    private static void login(){
        try {
            var env = Dotenv.load();
            String email = env.get("EMAIL");
            String password = env.get("PASSWORD");

            var emailInputEl = driver.findElement(By.name("email"));
            emailInputEl.sendKeys(email);
            pause(1000);
            var signInButton = driver.findElement(By.cssSelector("[data-testid='sign-in-button']"));
            signInButton.click();
            pause(2000);

            var passwordInputEl = driver.findElement(By.name("password"));
            passwordInputEl.sendKeys(password);
            pause(1000);
            var submitPasswordButton = driver.findElement(By.cssSelector("[data-testid='submit-password']"));
            submitPasswordButton.click();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private static void searchAndFilter() {
        try {
            pause(13000);
//             check for "Make my Profile Visible" dialog
            var isProfileHiddenDialog = driver.findElement(By.className("fe-button-leter"));
            if(isProfileHiddenDialog.isDisplayed()) {
                isProfileHiddenDialog.click();
                pause(2000);
            }

            var searchBox = driver.findElement(By.cssSelector("[type='search']"));
//            var searchBox = driver.findElement(By.cssSelector("[name='q']"));
            searchBox.sendKeys(String.join(" OR ", keywords));
            pause(1000);
            searchBox.sendKeys(Keys.ENTER);
            pause(5000);

            // Filters
            var postedDateFilter = driver.findElement(By.xpath( "//button[contains(text(), 'Today')]"));
            postedDateFilter.click();
            pause();

            var easyApplyFilter = driver.findElement(By.cssSelector("[aria-label='Filter Search Results by Easy Apply']"));
            easyApplyFilter.click();
            pause();

//            var willingToSponsor = driver.findElement(By.cssSelector("[aria-label='Filter Search Results by Work Authorization']"));
//            willingToSponsor.click();
//            pause();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void fillAndApply() {
        try {
            int totalJobCount = Integer.parseInt(driver.findElement(By.cssSelector("span[id='totalJobCount']")).getText());
            int maxPagination = (int)Math.ceil((float)totalJobCount / (float)numberOfJobsPerPage);
            int currentPage = 1;
            int currentJobIndex = 0;
            while (currentPage <= maxPagination) {
                for (int i=0; i<numberOfJobsPerPage; i++) {
                    pause(1000);
                    if (currentJobIndex >= totalJobCount){
                        System.out.println("\nThat's all the available jobs, adjust filters and try again");
                        break;
                    }
                    System.out.println("\nJob N°: (" + (currentJobIndex+1) + " out of " + totalJobCount + ")");
                    currentJobIndex++;
                    var currentJob = driver.findElement(By.cssSelector("dhi-search-card[data-cy-index='" + i + "']"));

                    var isAppliedRibbonVisible = currentJob.findElements(By.cssSelector("div.ribbon-status-applied"));
                    var jobTitle = currentJob.findElement(By.cssSelector("a[data-cy='card-title-link']"));

                    if (!isAppliedRibbonVisible.isEmpty()) {
                        System.out.println("Skipping \"" + jobTitle.getText() + "\". Reason: Already applied");
                        pause(1000);
                        continue;
                    }

                    // Scrolling
                    System.out.println("Scrolling.....");
                    var jsExecutor = (JavascriptExecutor) driver;
                    jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center'});", jobTitle);

                    System.out.println("Applying to " + jobTitle.getText() + "...");

                    var companyName = currentJob.findElement(By.cssSelector("[data-cy='search-result-company-name']"));
                    System.out.println("Company name: " + companyName.getText());
                    pause();

                    jobTitle.click();

                    // The job opens in a new tab so we have to account for that
                    var tab = switchToNewTab();
                    pause();

                    jobApplicationDriver(tab);
                    pause();
                    switchBackToDefaultTab(tab);
                    pause(1000);
                }

                System.out.println("\nFinished scrolling page N° " + currentPage);

                if (currentPage < maxPagination) {
                    var nextPageBtn = driver.findElement(By.xpath("//li[contains(@class, 'pagination-page') and not(contains(@class, 'active'))]//a[text()='" + (currentPage + 1) + "']"));
                    nextPageBtn.click();
                    pause(2000);
                }

                currentPage++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void jobApplicationDriver(Object[] windowHandles) {
        try {
            // the `Easy Apply` button is embedded in the shadow DOM so we have to account for that
            var shadowRoot = driver.findElement(By.cssSelector("apply-button-wc")).getShadowRoot();

            var applicationSubmitted = shadowRoot.findElements(By.cssSelector("application-submitted"));
            if (applicationSubmitted.isEmpty()) {
                var easyApplyBtn = shadowRoot.findElement(By.cssSelector("button.btn-primary"));
                easyApplyBtn.click();
                pause();

                // there could be several steps, so we use that to apply
                var steps = driver.findElement(By.cssSelector("progress-bar")).getDomAttribute("label");
                assert steps != null;
                String[] split = steps.split(" ");
                int start = Integer.parseInt(split[1]);
                int stop = Integer.parseInt(split[3]);

                while (start <= stop) {
                    var nextBtn = driver.findElement(By.cssSelector("button.btn-next"));
                    nextBtn.click();
                    int timeoutInSeconds = 30;
                    boolean canProceed = false;
                    for (int n = 0; n < timeoutInSeconds; n++) {
                        var errorMessages = driver.findElements(By.cssSelector("p.error-text"));
                        if (errorMessages.isEmpty()) {
                            canProceed = true;
                            break;
                        }
                        System.out.print("\rTime remaining: " + (timeoutInSeconds - n) + " seconds");
                        pause();
                    }
                    if (canProceed) {
                        pause(2000);
                        start++;
                    } else {
                        System.out.println("Inactivity detected, skipping this job...");
                        switchBackToDefaultTab(windowHandles);
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object[] switchToNewTab() {
        var windowHandles = driver.getWindowHandles().toArray();
        driver.switchTo().window((String) windowHandles[1]);
        return windowHandles;
    }

    private static void switchBackToDefaultTab(Object[] windowHandles) {
        driver.close();
        driver.switchTo().window((String) windowHandles[0]);
    }

    private static void pause() throws InterruptedException {
        Thread.sleep(3000);
    }

    private static void pause(int ms) throws InterruptedException {
        Thread.sleep(ms);
    }

}
